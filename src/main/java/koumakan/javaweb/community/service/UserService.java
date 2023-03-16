package koumakan.javaweb.community.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import koumakan.javaweb.community.dao.IUserDao;
import koumakan.javaweb.community.dao.UserMapper;
import koumakan.javaweb.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @Package: koumakan.javaweb.community.service
 * @Author: Alice Maetra
 * @Date: 2023/3/13 23:04
 * @Decription:
 */

@Service("userService")
@Scope("prototype")
public class UserService {

    @Autowired
    @Qualifier("alphaUserDao")
    private IUserDao _alphaUserDao;


    @Autowired
    @Qualifier("userMapper")
    private UserMapper userMapper;

    public User findUserById(int userId) {
        User user = userMapper.selectById(userId);
        return user;
    }


    @PostConstruct
    public void init() {

    }

    @PreDestroy
    public void destroy() {

    }

    public String useDao() {
        return this._alphaUserDao.toString();

    }
}
