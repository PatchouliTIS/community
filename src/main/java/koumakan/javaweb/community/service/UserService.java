package koumakan.javaweb.community.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.mail.MessagingException;
import koumakan.javaweb.community.dao.IUserDao;
import koumakan.javaweb.community.dao.UserMapper;
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

    @Autowired
    private TemplateEngine templateEngine;

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
        }else if(user.getActivationCode().equals(activation_code)) {
            // 将用户激活码对比
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
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
