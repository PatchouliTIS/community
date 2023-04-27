package koumakan.javaweb.community.controller;

import jakarta.annotation.Resource;
import koumakan.javaweb.community.annotation.LoginAuthority;
import koumakan.javaweb.community.entity.Page;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.FollowService;
import koumakan.javaweb.community.service.UserService;
import koumakan.javaweb.community.util.CommunityConstant;
import koumakan.javaweb.community.util.CommunityUtil;
import koumakan.javaweb.community.util.HostHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Package: koumakan.javaweb.community.controller
 * @Author: Alice Maetra
 * @Date: 2023/4/27 14:59
 * @Decription:
 */
@Controller
public class FollowController {

    @Resource
    private FollowService followService;

    @Resource
    private HostHandler hostHandler;

    @Resource
    private UserService userService;


    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    @LoginAuthority
    public String follow(int entityType, int entityId) {
        User user = hostHandler.getUser();

        followService.follow(user.getId(), entityType,entityId);

        System.out.println("关注成功！");

        return CommunityUtil.getJSONString(0, "关注成功！");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    @LoginAuthority
    public String unfollow(int entityType, int entityId) {
        User user = hostHandler.getUser();

        followService.unfollow(user.getId(), entityType,entityId);

        System.out.println("已取消关注！");

        return CommunityUtil.getJSONString(0, "已取消关注！");
    }


    /**
     * 显示某个用户的关注列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if(user == null) {
            throw new IllegalArgumentException("当前用户不存在！");
        }

        model.addAttribute("user", user);

        page.setLimit(3);
        page.setPath("/followees/" + userId);
        page.setRows((int)followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));

        List<Map<String , Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if(userList != null) {
            for (Map<String, Object> map : userList) {
                User followees = (User) map.get("user");
                User curUser = hostHandler.getUser();
                boolean isCurFollowed =
                        curUser != null && followService.isFollowed(
                                curUser.getId(),
                                CommunityConstant.ENTITY_TYPE_USER,
                                followees.getId()
                        );
                map.put("hasFollowed", isCurFollowed);
            }

        }

        model.addAttribute("users", userList);

        return "/site/followee";
    }


    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if(user == null) {
            throw new IllegalArgumentException("当前用户不存在！");
        }

        model.addAttribute("user", user);

        page.setLimit(3);
        page.setPath("/followers/" + userId);
        page.setRows((int)followService.findFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId));

        List<Map<String , Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if(userList != null) {
            for (Map<String, Object> map : userList) {
                User followers = (User) map.get("user");
                User curUser = hostHandler.getUser();
                boolean isCurFollowed =
                        curUser != null && followService.isFollowed(
                                curUser.getId(),
                                CommunityConstant.ENTITY_TYPE_USER,
                                followers.getId()
                        );
                map.put("hasFollowed", isCurFollowed);
            }

        }

        model.addAttribute("users", userList);

        return "/site/follower";
    }



}
