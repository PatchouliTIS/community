package koumakan.javaweb.community.controller;

import koumakan.javaweb.community.entity.DiscussPost;
import koumakan.javaweb.community.entity.Page;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.DiscussPostService;
import koumakan.javaweb.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @Package: koumakan.javaweb.community.controller
 * @Author: Alice Maetra
 * @Date: 2023/3/16 16:48
 * @Decription:
 *      视图层 网页主页
 */

@Controller
public class HomePageController {

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    @Qualifier("discussPostService")
    private DiscussPostService discussPostService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        //TODO: 动态替换

        // 本方法调用之前，SpringMVC会自动实例化Entity中的类以及Model，并直接将Entity实例注入Thymeleaf中
        // 因此可以在html中直接调用page
        page.setRows(discussPostService.findPostsCnt(0));
        page.setPath("/index");

        List<DiscussPost> listPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(listPosts.isEmpty() == false) {
            for(DiscussPost cur : listPosts) {
                Map<String, Object> tmp = new HashMap<>();
                tmp.put("post", cur);
                User user = userService.findUserById(cur.getUserId());
                tmp.put("user", user);
                discussPosts.add(tmp);
            }
        }


        // System.out.println(">>>Model Check Zone   current discussPosts:" + discussPosts.size());
        // System.out.println(discussPosts);

        model.addAttribute("discussPosts", discussPosts);

        // System.out.println(">>>Complete");

        // 必须与路径名一样 因为Thymeleaf需要使用这一字符串去templates中寻找对象
        return "/index";
    }


    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }
}
