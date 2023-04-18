package koumakan.javaweb.community.controller;

import com.alibaba.fastjson2.JSONPathCompilerReflect;
import jakarta.annotation.Resource;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.LikeService;
import koumakan.javaweb.community.util.CommunityUtil;
import koumakan.javaweb.community.util.HostHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;



/**
 * @Package: koumakan.javaweb.community.controller
 * @Author: Alice Maetra
 * @Date: 2023/4/18 9:17
 * @Decription:
 */
@Controller
public class LikeController {

    @Resource
    private LikeService likeService;

    @Resource
    private HostHandler hostHandler;


    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        // TODO: 设置拦截器查看是否参数存在。
        User user = hostHandler.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);

        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        // 以JSON格式返回结果
        Map<String, Object> map = new HashMap<>(){{
            put("likeCount", likeCount);
            put("likeStatus", likeStatus);
        }};

        return CommunityUtil.getJSONString(0, null, map);

    }

}
