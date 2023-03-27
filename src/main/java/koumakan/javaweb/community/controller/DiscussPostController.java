package koumakan.javaweb.community.controller;

import koumakan.javaweb.community.dao.DiscussPostMapper;
import koumakan.javaweb.community.entity.DiscussPost;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.DiscussPostService;
import koumakan.javaweb.community.util.CommunityUtil;
import koumakan.javaweb.community.util.HostHandler;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.PartHttpMessageWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

/**
 * @Package: koumakan.javaweb.community.controller
 * @Author: Alice Maetra
 * @Date: 2023/3/27 16:11
 * @Decription:
 */

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHandler hostHandler;


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
}
