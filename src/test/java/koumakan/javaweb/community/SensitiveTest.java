package koumakan.javaweb.community;

import koumakan.javaweb.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Package: koumakan.javaweb.community
 * @Author: Alice Maetra
 * @Date: 2023/3/27 14:36
 * @Decription:
 */


@SpringBootTest
public class SensitiveTest {


    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void sensitiveTest() {
        String text = new String("114514赌博嫖娼拉屎赌博");
        String filtered = sensitiveFilter.filter(text);

        System.out.println(text + " after: " + filtered);
    }
}
