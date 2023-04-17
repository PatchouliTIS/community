package koumakan.javaweb.community.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @Package: koumakan.javaweb.community.config
 * @Author: Alice Maetra
 * @Date: 2023/4/17 20:31
 * @Decription:
 */

@Configuration
public class RedisConfig {



    /**
     *
     * @param factory 在Spring创建当前Bean容器时，如果当前Bean有依赖项，则自动注入。
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // Redis数据库连接
        template.setConnectionFactory(factory);

        // 设置Key的序列化方式
        template.setKeySerializer(RedisSerializer.string());

        // 设置Value的序列化方式
        template.setValueSerializer(RedisSerializer.json());

        // 设置hash的key的~
        template.setHashKeySerializer(RedisSerializer.string());

        // 设置hash的value的~
        template.setHashValueSerializer(RedisSerializer.json());

        // 触发设置生效
        template.afterPropertiesSet();

        return template;
    }
}
