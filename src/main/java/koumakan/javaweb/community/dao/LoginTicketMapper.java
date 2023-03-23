package koumakan.javaweb.community.dao;

import koumakan.javaweb.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @Package: koumakan.javaweb.community.dao
 * @Author: Alice Maetra
 * @Date: 2023/3/22 18:56
 * @Decription:
 */


@Mapper
public interface LoginTicketMapper {


    // TODO: 使用MyBatis中的#{}
    @Insert({
            "insert into login_ticket(user_id, ticket, status, expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket ",
            "where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "<script>",
            "update login_ticket set status=#{status} ",
            "<if test=\"ticket!=null\">",
            "and 1=1 ",
            "</if>",
            "where ticket=#{ticket} ",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
