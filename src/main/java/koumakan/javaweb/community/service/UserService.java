package koumakan.javaweb.community.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.mail.MessagingException;
import koumakan.javaweb.community.dao.IUserDao;
import koumakan.javaweb.community.dao.LoginTicketMapper;
import koumakan.javaweb.community.dao.UserMapper;
import koumakan.javaweb.community.entity.LoginTicket;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.util.CommunityConstant;
import koumakan.javaweb.community.util.CommunityUtil;
import koumakan.javaweb.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Package: koumakan.javaweb.community.service
 * @Author: Alice Maetra
 * @Date: 2023/3/13 23:04
 * @Decription:
 */

@Service("userService")
// @Scope("prototype")
public class UserService implements CommunityConstant {

    @Autowired
    @Qualifier("alphaUserDao")
    private IUserDao _alphaUserDao;


    /**
     * 将当前注册用户跟数据库中的用户比较，判断是否重复
     */
    @Autowired
    @Qualifier("userMapper")
    private UserMapper userMapper;

    @Autowired
    @Qualifier("mailClient")
    private MailClient mailClient;


    /**
     * 生成Thymeleaf动态模板
     */
    @Autowired
    private TemplateEngine templateEngine;


    @Autowired
    private LoginTicketMapper loginTicketMapper;


    /**
     * 通过SpringBeanFactory分析配置文件，获取配置文件中的值的信息。
     * TODO: 换成${}
     */
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int userId) {
        User user = userMapper.selectById(userId);
        return user;
    }

    public Map<String, Object> register(User user) throws MessagingException {
        Map<String, Object> map = new HashMap<>();

        // 空值判断处理
        if(user == null) {
            throw new IllegalArgumentException("参数为空！");
        }
        if(StringUtils.isBlank((user.getUsername()))) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())) {
            map.put("userpswdMsg", "密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("useremailMsg", "邮箱不能为空！");
            return map;
        }


        // 验证用户
        User targetUser = userMapper.selectByName(user.getUsername());
        if(targetUser != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }
        // 验证邮箱
        targetUser = userMapper.selectByEmail(user.getEmail());
        if(targetUser != null) {
            map.put("useremailMsg", "该邮箱已被注册！");
            return map;
        }

        // 注册用户
        // 使用CommunityUtil工具类进行自动UUID生成每个用户的salt。将其加入密码中去
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png" , new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        // 记得插入数据库
        userMapper.insertUser(user);


        // 激活邮件
        Context context = new Context();
        // 1. 设置目标用户邮箱
        context.setVariable("email", user.getEmail());
        // 2. 设置激活连接
        // userID什么时候设置的？MyBatis自动生成数据行（类的实例对象）的主键ID
        String actUrl = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", actUrl);
        String content = templateEngine.process("/mail/activation", context);
        // 3. 发送激活邮件
        mailClient.sendMail(user.getEmail(), "Activate your Account! ", content);


        // 如果map是空的，那么就说明没有错误
        return map;
    }

    /**
     * 激活用户账号的业务代码
     * @param userId
     * @param activation_code
     * @return
     */
    public int activation(int userId, String activation_code) {
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(activation_code) == true) {
            // 将用户激活码对比
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }


    public Map<String, Object> login(String username, String password, int expired) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if(StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名为空！");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("userpswdMsg", "密码为空！");
            return map;
        }

        // 验证账号
        User curUser = userMapper.selectByName(username);
        if(curUser == null) {
            map.put("usernameMsg", "该用户不存在！");
            return map;
        }
        // 没有激活！
        if(curUser.getStatus() == 0) {
            map.put("usernameMsg", "该用户仍未激活！");
            return map;
        }
        // 验证密码
        // 因为传入的密码是明文。需要进行MD5加密后再与user中密码进行比较
        // 记得加上salt
        password = CommunityUtil.md5(password + curUser.getSalt());
        if(password.equals(curUser.getPassword()) == false) {
            map.put("userpswdMsg", "密码输入错误！");
            return map;
        }


        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(curUser.getId());
        //  status 设为 0，在loginTicket表中是有效
        loginTicket.setStatus(0);
        // 生成随机登录凭证
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expired * 1000));

        loginTicketMapper.insertLoginTicket(loginTicket);

        // 将ticket作为用户的一种session用于从loginTicket表中快速获取对应用户的登录认证
        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }


    public LoginTicket findLoginTicketByTicket (String ticket) {
        if(StringUtils.isBlank(ticket)) {
            throw new IllegalArgumentException("TICKET为空！");
        }

        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);

        return loginTicket;
    }


    @PostConstruct
    public void init() {

    }

    @PreDestroy
    public void destroy() {

    }

    public String useDao() {
        return this._alphaUserDao.toString();
    }
}
