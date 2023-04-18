package koumakan.javaweb.community.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Package: koumakan.javaweb.community.util
 * @Author: Alice Maetra
 * @Date: 2023/3/23 19:34
 * @Decription:
 *      用于取得浏览器发来cookie中的ticket
 */

public class CookieUtil {

    /**
     *
     * @param request 是从浏览器的request请求中获取数据
     * @return
     */
    public static String getTicket(HttpServletRequest request, String arg) {


        // 首次登录的用户必然没有cookie 过期了
        if(request == null || StringUtils.isBlank(arg)) {
            if(request == null) {
                throw new IllegalArgumentException("request为空！");
            }

            if(StringUtils.isBlank(arg)) {
                throw new IllegalArgumentException("参数为空！");
            }

        }


        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            for(Cookie c : cookies) {
                if(c.getName().equals(arg)) {
                    return c.getValue();
                }
            }
        }

        return null;
    }
}
