package koumakan.javaweb.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

/**
 * @Package: koumakan.javaweb.community
 * @Author: Alice Maetra
 * @Date: 2023/3/20 16:29
 * @Decription:
 *          Logback 试用
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);


    @Test
    public void testLogger() {
        System.out.println(logger.getName());
        logger.debug(">>>debug set<<<");
        logger.info("info");
        logger.warn("warn");

        logger.error("error");
    }
}
