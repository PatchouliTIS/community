package koumakan.javaweb.community.controller.interceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import koumakan.javaweb.community.entity.Message;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.MessageService;
import koumakan.javaweb.community.util.HostHandler;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Package: koumakan.javaweb.community.controller.interceptor
 * @Author: Alice Maetra
 * @Date: 2023/5/18 20:49
 * @Decription:
 */


@Component
public class NoticeInterceptor implements HandlerInterceptor {

    @Resource
    private HostHandler hostHandler;

    @Resource
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        User user = hostHandler.getUser();
        if(user != null && modelAndView != null) {
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }

}
