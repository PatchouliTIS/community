package koumakan.javaweb.community.service;

import koumakan.javaweb.community.dao.CommentMapper;
import koumakan.javaweb.community.dao.DiscussPostMapper;
import koumakan.javaweb.community.entity.Comment;
import koumakan.javaweb.community.util.CommunityConstant;
import koumakan.javaweb.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @Package: koumakan.javaweb.community.service
 * @Author: Alice Maetra
 * @Date: 2023/3/28 14:11
 * @Decription:
 */


@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> getAllComments(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }


    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    public Comment findCommentById (int id) {
        return commentMapper.selectCommentById(id);
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        // 1. 过滤标题和内容的敏感词
        if(comment == null || StringUtils.isBlank(comment.getContent())) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 1.1 转换HTML
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        // 1.2 过滤敏感词
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        int insertRow = commentMapper.insertComment(comment);
        // 更新帖子的评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST) {
            int cnt = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            // comment.getEntityId() 保存的是帖子ID post.id
            discussPostService.updateCommentCount(comment.getEntityId(), cnt);
        }

        return insertRow;
    }
}
