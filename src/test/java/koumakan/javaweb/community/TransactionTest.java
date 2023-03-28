package koumakan.javaweb.community;

import koumakan.javaweb.community.service.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Package: koumakan.javaweb.community
 * @Author: Alice Maetra
 * @Date: 2023/3/28 13:50
 * @Decription:
 */

@SpringBootTest
public class TransactionTest {
    @Autowired
    private DemoService demoService;

    @Test
    public void testTrans() {
        demoService.addPost(0, "114", "541");
    }
}
