package koumakan.javaweb.community.controller;

import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import koumakan.javaweb.community.dao.LoginTicketMapper;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.UserService;
import koumakan.javaweb.community.util.CommunityConstant;
import koumakan.javaweb.community.util.CommunityUtil;
import koumakan.javaweb.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static koumakan.javaweb.community.util.CommunityConstant.DEFAULT_EXPIRED_SECONDS;
import static koumakan.javaweb.community.util.CommunityConstant.REMEMBER_EXPIRED_SECONDS;

/**
 * @Package: koumakan.javaweb.community.controller
 * @Author: Alice Maetra
 * @Date: 2023/3/21 14:46
 * @Decription:
 *      登录页面开发
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String ROOT_PATH;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * 返回给前端页面
     * @return
     */
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        // System.out.println(">>>register");
        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }


    /**
     * 注意：当发起一个POST请求时我们没有给参数定义一个Entity实体类，那么
     * SpringMVC就不会自动将这些零散变量装入Model中去。我们需要：
     *      1. 手动在本方法内部进行Model的手动装载
     *      2. 在html文件中使用 ${param.[param_id]} 方法获取表单数据
     *          这种方法的param_id需要和本方法中定义的参数名一致
     *
     *
     * @param model
     * @param //httpSession
     * @param response  将服务器生成的session对应的ticket通过cookie传递给客户端让Client保存
     * @param username
     * @param password
     * @param code
     * @param rememberMe
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(Model model
            // , HttpSession httpSession
            , @CookieValue("kaptchaOwner") String kaptchaOwner
            , HttpServletResponse response
            , String username
            , String password
            , String code
            , boolean rememberMe) {
        // String kaptcha = (String) httpSession.getAttribute("kaptcha");
        // 从Redis中获取验证码进行登录
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)) {
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }


        // 验证码的检查
        if(StringUtils.isBlank(kaptcha)
                || StringUtils.isBlank(code)
                || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }

        // 验证账号和密码
        // 因为登录凭证都是放在数据库中保存，因此《记住我》这一勾选项目只是决定了
        // 登录凭证的expired，也就是超时时间的长短
        // 1. 设置登录认证凭证超时时间
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        // 2. 登录，使用userService的功能
        Map<String, Object> map = userService.login(username, password, expiredSeconds);

        if(map.containsKey("ticket")) {
            // 使用Cookie保存loginTicket
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(ROOT_PATH);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAllAttributes(map);
            // model.addAttribute("usernameMsg", map.get("usernameMsg"));
            // model.addAttribute("userpswdMsg", map.get("userpswdMsg"));
            // model.addAttribute("userEntityMsg", map.get("userEntityMsg"));
            // model.addAttribute("userActivationMsg", map.get("userActivationMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        // TODO: 前端使用MD5加密密码传输。
        userService.logout(ticket);
        return "redirect:/login";
    }




    /**
     * 前端的表单获取用户注册的数据，传递给表现层Controller后读取到对应的name属性中去
     * @param model
     * @param user 前端页面通过 <input ... > 来获取User实体类中对应的成员属性数据。
     * @return
     */
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) throws MessagingException {
        // TODO: 前端使用MD5加密密码传输。

        Map<String, Object> map = userService.register(user);

        if(map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已发送邮件。");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }

        model.addAllAttributes(map);
        // 注册出问题，用户已存在
        return "/site/register";
    }


    @RequestMapping(path = "/activation/{userId}/{activation_code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("activation_code") String code) {

        System.out.println(">>> ENTERING ACTIVATION <<<");
        int result = userService.activation(userId, code);

        if (result == userService.ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，可以登陆");
        } else if (result == userService.ACTIVATION_REPEAT) {
            model.addAttribute("msg", "激活失败，重复激活");
        } else {
            model.addAttribute("msg", "激活失败，激活码不正确");
        }

        model.addAttribute("target", "/login");

        return "/site/operate-result";

    }


    /**
     * 获取当前session中对应用户的验证码
     * 最初是使用session存储验证码；
     * 现在重构，使用Redis存储验证码。
     * @param response
     * @param //session
     * @throws IOException
     */
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response
            // , HttpSession session
    ) throws IOException {
        // 1. 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 2.1 将验证码存入session
        // session.setAttribute("kaptcha", text);

        // 2.2 生成验证码的归属者，也就是当前登录的用户
        //    保存到Cookie中
        String kaptchaOwner= CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(ROOT_PATH);

        response.addCookie(cookie);

        // 2.3 生成RedisKey，并写入数据库
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);

        // 3. 将验证码图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
