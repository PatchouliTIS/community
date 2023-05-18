package koumakan.javaweb.community.config;

import jakarta.annotation.Resource;
import koumakan.javaweb.community.annotation.LoginAuthority;
import koumakan.javaweb.community.controller.interceptor.DemoInterceptor;
import koumakan.javaweb.community.controller.interceptor.LoginAuthorityInterceptor;
import koumakan.javaweb.community.controller.interceptor.LoginTicketInterceptor;
import koumakan.javaweb.community.controller.interceptor.NoticeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Package: koumakan.javaweb.community.config
 * @Author: Alice Maetra
 * @Date: 2023/3/23 18:19
 * @Decription:
 *      WebMvcConfigurer 用于设置拦截器所拦截的目标路径和文件
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private DemoInterceptor demoInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginAuthorityInterceptor loginAuthorityInterceptor;


    @Resource
    private NoticeInterceptor noticeInterceptor;

    /**
     * 演示如何使用WebMvc的register
     * @param registry 参数自动由SpringMVC传入，然后用这一变量去配置我们的interceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(demoInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.js")
                .addPathPatterns("/register", "/login");
        // WebMvcConfigurer.super.addInterceptors(registry);

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.js");

        registry.addInterceptor(loginAuthorityInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.js");

        registry.addInterceptor(noticeInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.js");
    }






}
