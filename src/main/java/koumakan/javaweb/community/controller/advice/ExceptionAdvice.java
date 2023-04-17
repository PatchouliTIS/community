package koumakan.javaweb.community.controller.advice;

import ch.qos.logback.classic.model.LevelModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import koumakan.javaweb.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Package: koumakan.javaweb.community.controller.advice
 * @Author: Alice Maetra
 * @Date: 2023/4/3 16:47
 * @Decription:
 *      Spring 提供的 ControllerAdvice 类注解：
 *          1. ExceptionHandler
 *          2. ModelAttribute
 *          3. DataBinder
 *
 */

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.error("服务器发生异常" + e.getMessage());
        for(StackTraceElement element : e.getStackTrace()) {
            LOGGER.error(element.toString());
        }


        String requestWith = request.getHeader("x-requested-with");
        // 网站请求的是 JSON / XML 格式的数据
        if (requestWith.equals("XMLHttpRequest")) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
