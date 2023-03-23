package koumakan.javaweb.community.controller;

import com.google.code.kaptcha.Producer;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

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
     * 前端的表单获取用户注册的数据，传递给表现层Controller后读取到对应的name属性中去
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) throws MessagingException {
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



    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        // 1. 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 2. 将验证码存入session
        session.setAttribute("kaptcha", text);

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
