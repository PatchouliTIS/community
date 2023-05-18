package koumakan.javaweb.community.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Package: koumakan.javaweb.community.aspect
 * @Author: Alice Maetra
 * @Date: 2023/4/3 17:38
 * @Decription:
 */

@Component
@Aspect
public class ServiceLoginAspect {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceLoginAspect.class);


    /**
     * 声明需要进行加强的方法，通过 execution() 声明
     */
    @Pointcut("execution(* koumakan.javaweb.community.service.*.*(..))")
    public void pointcut() {

    }


    /**
     * 对所有用户的操作进行日志记录
     * 例：用户[1.2.3.4],在[xxx],访问了[com.nowcoder.community.service.xxx()].
     * @param joinPoint 使用JoinPoint连接点（也就是目标类对应的需要增强的目标方法）
     */
    @Before("pointcut()")
    public void loginInfo(JoinPoint joinPoint) {
        String path = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) {
            return ;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        LOGGER.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, path));
    }
}
