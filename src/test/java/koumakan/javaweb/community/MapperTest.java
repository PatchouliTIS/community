package koumakan.javaweb.community;

import koumakan.javaweb.community.dao.DiscussPostMapper;
import koumakan.javaweb.community.dao.LoginTicketMapper;
import koumakan.javaweb.community.dao.UserMapper;
import koumakan.javaweb.community.entity.DiscussPost;
import koumakan.javaweb.community.entity.LoginTicket;
import koumakan.javaweb.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Package: koumakan.javaweb.community
 * @Author: Alice Maetra
 * @Date: 2023/3/16 15:20
 * @Decription:
 *          test UserMapper
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    public void testSelectPosts() {

        List<DiscussPost> posts = (ArrayList<DiscussPost>)discussPostMapper.selectPosts(0 , 0, 20);

        posts.forEach(p -> {
            System.out.println(p);
        });


        int rows = discussPostMapper.selectPostsCnt(2);
        System.out.println(rows);
    }


    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }


}
