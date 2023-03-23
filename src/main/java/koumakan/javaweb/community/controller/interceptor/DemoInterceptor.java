package koumakan.javaweb.community.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Package: koumakan.javaweb.community.controller.interceptor
 * @Author: Alice Maetra
 * @Date: 2023/3/23 18:13
 * @Decription:
 */

@Component
public class DemoInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DemoInterceptor.class);

    /**
     * 在Controller之前执行
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandle: " + handler.toString());
        // 就是return true
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }


    /**
     * 在Controller之后执行
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle: " + handler.toString());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }


    /**
     * 在TemplateEngine之后执行
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion: " + handler.toString());
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
