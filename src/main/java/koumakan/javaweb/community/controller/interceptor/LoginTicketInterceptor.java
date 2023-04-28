package koumakan.javaweb.community.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import koumakan.javaweb.community.dao.LoginTicketMapper;
import koumakan.javaweb.community.dao.UserMapper;
import koumakan.javaweb.community.entity.LoginTicket;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.UserService;
import koumakan.javaweb.community.util.CookieUtil;
import koumakan.javaweb.community.util.HostHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @Package: koumakan.javaweb.community.controller.interceptor
 * @Author: Alice Maetra
 * @Date: 2023/3/23 19:32
 * @Decription:
 */


@Component
public class LoginTicketInterceptor implements HandlerInterceptor {


    @Autowired
    private UserService userService;


    @Autowired
    private HostHandler hostHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 获取用户cookie
        String ticket = CookieUtil.getTicket(request, "ticket");

        // 对于首次登录的用户，ticket必然为空，因此不需要抛出异常
        // if(StringUtils.isBlank(ticket)) {
        //     throw new IllegalArgumentException("TICKET 为空！");
        // }

        if(ticket != null) {
            // 2. 服务器通过cookie发起查询获得用户信息
            LoginTicket loginTicket = userService.findLoginTicketByTicket(ticket);

            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                User user = userService.findUserById(loginTicket.getUserId());
                // 3. 使用ThreadLocal在服务器端，此次session（对话）持续时间内
                hostHandler.setUser(user);
            }
        }



        return HandlerInterceptor.super.preHandle(request, response, handler);
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHandler.getUser();
        System.out.println("post Handler");
        if(user != null && modelAndView != null) {
            System.out.println("get user");
            modelAndView.addObject("loginUser", user);
        }
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHandler.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
