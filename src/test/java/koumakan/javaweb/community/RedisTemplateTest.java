package koumakan.javaweb.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;

/**
 * @Package: koumakan.javaweb.community
 * @Author: Alice Maetra
 * @Date: 2023/4/17 21:25
 * @Decription:
 */

@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString() {
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey, 1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey, 14));
        System.out.println(redisTemplate.opsForList().size(redisKey));
    }



    @Test
    public void testHash() {
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey, "ID", 114514);
        redisTemplate.opsForHash().put(redisKey, "name", "YSJP");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "ID"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "name"));
    }


    @Test
    public void testList() {
        String redisKey = "test:idx";

        redisTemplate.opsForList().remove(redisKey, -1, 114);

        redisTemplate.opsForList().leftPush(redisKey, 114);
        redisTemplate.opsForList().leftPush(redisKey, 514);




        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 10));
        System.out.println(redisTemplate.opsForList().size(redisKey));
    }


    @Test
    public void testBoundOPs() {
        String redisKey = "test:bound";

        BoundValueOperations bop = redisTemplate.boundValueOps(redisKey);
        bop.set("haha", 1);
    }



    @Test
    public void testTransaction() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:trans";

                operations.multi();

                operations.opsForSet().add(redisKey, "114");
                operations.opsForSet().add(redisKey, "514");
                operations.opsForSet().add(redisKey, "YJSP");

                return operations.exec();
            }
        });

        System.out.println(obj);
    }
}
