package koumakan.javaweb.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @Package: koumakan.javaweb.community.config
 * @Author: Alice Maetra
 * @Date: 2023/3/13 23:14
 * @Decription:
 */

@Configuration
public class AlphaConfig {

    @Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
    }
}
