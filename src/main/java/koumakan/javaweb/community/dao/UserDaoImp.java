package koumakan.javaweb.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @Package: koumakan.javaweb.community.dao
 * @Author: Alice Maetra
 * @Date: 2023/3/13 22:49
 * @Decription:
 *      Implementations
 */

@Repository("userDao")
@Primary
public class UserDaoImp implements IUserDao{
    @Override
    public String select() {
        return "Hibernate";
    }
}
