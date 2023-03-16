package koumakan.javaweb.community.service;

import koumakan.javaweb.community.dao.DiscussPostMapper;
import koumakan.javaweb.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Package: koumakan.javaweb.community.service
 * @Author: Alice Maetra
 * @Date: 2023/3/16 16:37
 * @Decription:
 *      service层，调用MapperDAO而不要直接调用实体类，降低系统间的耦合度。
 */

@Service
public class DiscussPostService {

    @Autowired
    @Qualifier("discussPostMapper")
    private DiscussPostMapper discussPostMapper;


    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectPosts(userId, offset, limit);
    }

    public int findPostsCnt(int userId) {
        return discussPostMapper.selectPostsCnt(userId);
    }
}
