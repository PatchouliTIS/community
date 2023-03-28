package koumakan.javaweb.community.controller;

import koumakan.javaweb.community.entity.Comment;
import koumakan.javaweb.community.entity.DiscussPost;
import koumakan.javaweb.community.entity.Page;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.CommentService;
import koumakan.javaweb.community.service.DiscussPostService;
import koumakan.javaweb.community.service.UserService;
import koumakan.javaweb.community.util.CommunityConstant;
import koumakan.javaweb.community.util.CommunityUtil;
import koumakan.javaweb.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Package: koumakan.javaweb.community.controller
 * @Author: Alice Maetra
 * @Date: 2023/3/27 16:11
 * @Decription:
 */

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHandler hostHandler;

    @Autowired
    private CommentService commentService;


    /**
     * 发布帖子
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHandler.getUser();
        if(user == null) {
            return CommunityUtil.getJSONString(403, "仍未登录！");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        System.out.println(">>>add post success<<<");

        // 如果有报错 后续统一处理
        return CommunityUtil.getJSONString(0, "发布成功！");
    }


    /**
     * 查询帖子详情的新页面
     * @param model
     * @param postId
     * @return
     */
    @RequestMapping(path = "/detail/{postId}", method = RequestMethod.GET)
    public String getDiscussPost(Model model, @PathVariable("postId") int postId, Page page) {
        // 帖子本体
        DiscussPost post = discussPostService.findPost(postId);
        model.addAttribute("post", post);

        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        // 评论
        // 1. 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + postId);
        page.setRows(post.getCommentCount());

        // 2. 调用service
        // 获取帖子评论
        List<Comment> commentLists =  commentService.getAllComments(
                ENTITY_TYPE_POST,
                post.getId(),
                page.getOffset(),
                page.getLimit());
        System.out.println("current post's reply:"+ commentLists);
        List<Map<String, Object>> commentViewList = new ArrayList<>();
        if(commentLists != null) {
            for (Comment comment : commentLists) {
                // 装载每个帖子评论
                Map<String, Object> commentView = new HashMap<>();
                commentView.put("user", userService.findUserById(comment.getUserId()));
                commentView.put("comment", comment);

                // 获取当前评论的评论
                List<Comment> replyLists = commentService.getAllComments(
                        ENTITY_TYPE_COMMENT,
                        comment.getId(),
                        0,      // 不分页
                        Integer.MAX_VALUE);

                System.out.println("current comment's reply:"+ replyLists);

                List<Map<String, Object>> replyViewList = new ArrayList<>();
                if(replyLists != null) {
                    for(Comment rep : replyLists) {
                        // 装载当前评论的评论
                        Map<String, Object> replyView = new HashMap<>();
                        replyView.put("user", userService.findUserById(rep.getUserId()));
                        replyView.put("reply", rep);
                        // 装载 回复 动作的目标
                        User replyTarget = rep.getTargetId() == 0 ? null : userService.findUserById(rep.getTargetId());
                        replyView.put("target", replyTarget);

                        // 装载 评论回复 的数量
                        int repCnt = commentService.findCommentCount(ENTITY_TYPE_COMMENT, rep.getId());
                        replyView.put("count", repCnt);


                        replyViewList.add(replyView);
                    }
                }

                // 装载评论的评论
                commentView.put("replies", replyViewList);
                // 装载replies的数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentView.put("replyCount", replyCount);

                commentViewList.add(commentView);
            }
        }

        model.addAttribute("comments", commentViewList);

        return "/site/discuss-detail";
    }
}
