package koumakan.javaweb.community.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import koumakan.javaweb.community.annotation.LoginAuthority;
import koumakan.javaweb.community.util.HostHandler;
import org.springframework.beans.Mergeable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * @Package: koumakan.javaweb.community.controller.interceptor
 * @Author: Alice Maetra
 * @Date: 2023/3/24 18:33
 * @Decription:
 *      使用自定义注解，
 *      在需要登录权限验证的访问方法处添加注解，
 *      这样就能在访问敏感路径之前发起拦截并检验权限。
 *
 */

@Component
public class LoginAuthorityInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHandler hostHandler;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*TODO: 判断拦截的是不是一个方法，如果是则转型，并进行如下处理：
        * 1.  获取该handler的方法method；
        * 2.  获取该method的注解，如果属于LoginAuthority类，则继续处理：
        * 3.  如果属于L.A.类，且获取当前进程保存的User实体类发现为空，则当前请求的用户还没登陆，则拒绝
        *       访问（返回false），且使用response重定向到index页面。
        * 4.  将拦截器使用WebMvcConfigurer配置。
        * */

        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginAuthority loginAuthority = method.getAnnotation(LoginAuthority.class);
            if(loginAuthority != null) {
                Object user = hostHandler.getUser();
                if(user == null) {
                    // 没登录，不具有这种权限，返回false并重定向到index
                    response.sendRedirect(request.getContextPath() + "/login");
                    return false;
                }
            }
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
