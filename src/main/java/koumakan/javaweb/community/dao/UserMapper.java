package koumakan.javaweb.community.dao;

import koumakan.javaweb.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Package: koumakan.javaweb.community.dao
 * @Author: Alice Maetra
 * @Date: 2023/3/16 14:59
 * @Decription:
 *      MyBatis 写出Mapper的接口，MyBatis底层自动实现Mapper接口的
 *      实现类去读取并处理数据库中的数据，将其各个数据项的属性与实体类
 *      entity中的字段做一一映射
 */


// @Repository 也可以用于注解Mapper
    // 请注意，所有注解名只是用于程序员之间为了更好理解类的功能而
    // 进行的一种约定俗成的命名规则，没有强制要求
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

}
