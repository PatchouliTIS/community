package koumakan.javaweb.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @Package: koumakan.javaweb.community.dao
 * @Author: Alice Maetra
 * @Date: 2023/3/13 22:53
 * @Decription:
 */

@Repository("alphaUserDao")
public class AlphaUserDaoImp implements IUserDao{
    @Override
    public String select() {
        return "MyBatis";
    }
}
