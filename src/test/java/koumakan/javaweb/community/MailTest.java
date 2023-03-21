package koumakan.javaweb.community;

import com.sun.mail.util.MailConnectException;
import jakarta.mail.MessagingException;
import koumakan.javaweb.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


/**
 * @Package: koumakan.javaweb.community
 * @Author: Alice Maetra
 * @Date: 2023/3/20 18:23
 * @Decription:
 *      测试发送邮件功能
 */

@SpringBootTest
// @ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    private static Logger logger = LoggerFactory.getLogger(MailTest.class);

    @Test
    public  void testTextMail() {
        try {
            mailClient.sendMail("623894679@qq.com", "DDDR", "welcome, traveller");
        } catch (MessagingException e) {
            logger.error("send wrong" + e.getMessage());
        } finally {
            System.out.println("done");
        }
    }


    /**
     * 在测试类中使用thymeleaf模板如何使用动态变量
     */
    @Test
    public void testHtmlMail() {
        Context context = new Context();

        context.setVariable("username", "亚玛萨那度");

        String content = templateEngine.process("/mail/demo", context);

        try {
            mailClient.sendMail("623894679@qq.com", "魔兽世界HTML", content);
        } catch (MessagingException e) {
            logger.error("发送错误" + e.getMessage());
        } finally {
            System.out.println(content);
            System.out.println("done");
        }

    }
}
