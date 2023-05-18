package koumakan.javaweb.community.controller;

import jakarta.annotation.Resource;
import koumakan.javaweb.community.entity.Comment;
import koumakan.javaweb.community.entity.Event;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.event.EventConsumer;
import koumakan.javaweb.community.event.EventProducer;
import koumakan.javaweb.community.service.CommentService;
import koumakan.javaweb.community.service.DiscussPostService;
import koumakan.javaweb.community.util.CommunityConstant;
import koumakan.javaweb.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.multipart.PartHttpMessageWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @Package: koumakan.javaweb.community.controller
 * @Author: Alice Maetra
 * @Date: 2023/3/28 16:51
 * @Decription:
 */

@Controller
@RequestMapping(path = "/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    @Qualifier("eventProducer")
    private EventProducer eventProducer;
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHandler hostHandler;

    @Resource
    private DiscussPostService discussPostService;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        User user = hostHandler.getUser();
        if(user == null) {
            // ... 异常处理，权限认证
        }
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 装载事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(user.getId())
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setData("postId", discussPostId);
        // 分类讨论eventUserId的情况
        // 1. 帖子
        if (event.getEntityType() == ENTITY_TYPE_COMMENT) {
            event.setEntityUserId(discussPostService.findPost(event.getEntityId()).getUserId());
        }
        // 2. 评论
        else if (event.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(event.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        eventProducer.triggerEvent(event);

        return "redirect:/discuss/detail/" + discussPostId;
    }

}
