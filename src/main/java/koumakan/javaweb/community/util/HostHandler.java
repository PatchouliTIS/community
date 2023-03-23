package koumakan.javaweb.community.util;

import koumakan.javaweb.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @Package: koumakan.javaweb.community.util
 * @Author: Alice Maetra
 * @Date: 2023/3/23 20:14
 * @Decription:
 *      本次会话内持有用户信息，也就是user实体类，取代session作用
 */

@Component
public class HostHandler {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
