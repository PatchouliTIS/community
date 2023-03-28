package koumakan.javaweb.community.controller;

import koumakan.javaweb.community.entity.Comment;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.CommentService;
import koumakan.javaweb.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHandler hostHandler;

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

        return "redirect:/discuss/detail/" + discussPostId;
    }

}
