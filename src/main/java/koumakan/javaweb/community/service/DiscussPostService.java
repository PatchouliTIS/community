package koumakan.javaweb.community.service;

import koumakan.javaweb.community.dao.DiscussPostMapper;
import koumakan.javaweb.community.entity.DiscussPost;
import koumakan.javaweb.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.expression.Ids;

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

    @Autowired
    @Qualifier("sensitiveFilter")
    private SensitiveFilter sensitiveFilter;


    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectPosts(userId, offset, limit);
    }

    public int addDiscussPost(DiscussPost discussPost) {
        if(discussPost == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 处理标题和内容的敏感词
        // 1. 先将HTML标签中的符号<>转义，防止恶意注入
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));


        // 2. 处理敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));


        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public int findPostsCnt(int userId) {
        return discussPostMapper.selectPostsCnt(userId);
    }


    public DiscussPost findPost(int id) { return discussPostMapper.selectPostById(id); }

    public int updateCommentCount(int id, int count) {
        return discussPostMapper.updateCommentCount(id, count);
    }


}
