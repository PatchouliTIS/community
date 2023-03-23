package koumakan.javaweb.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Package: koumakan.javaweb.community.config
 * @Author: Alice Maetra
 * @Date: 2023/3/22 16:28
 * @Decription:
 */

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer() {
        // 1. 设置 Config 依赖的 Properties 对象
        Properties properties = new Properties();

        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "114,19,154");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789AFXCV");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.noise.impl", "com.googel.code.kaptcha.impl.NoNoise");


        DefaultKaptcha kaptcha = new DefaultKaptcha();

        Config config = new Config(properties);
        kaptcha.setConfig(config);

        return kaptcha;
    }
}
