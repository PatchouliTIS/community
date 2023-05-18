package koumakan.javaweb.community.controller;

import com.alibaba.fastjson2.JSONPathCompilerReflect;
import jakarta.annotation.Resource;
import koumakan.javaweb.community.entity.Event;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.event.EventProducer;
import koumakan.javaweb.community.service.LikeService;
import koumakan.javaweb.community.util.CommunityConstant;
import koumakan.javaweb.community.util.CommunityUtil;
import koumakan.javaweb.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class LikeController implements CommunityConstant {

    @Autowired
    @Qualifier("eventProducer")
    private EventProducer eventProducer;

    @Resource
    private LikeService likeService;

    @Resource
    private HostHandler hostHandler;


    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
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


        // 触发点赞事件
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.triggerEvent(event);
        }

        return CommunityUtil.getJSONString(0, null, map);

    }

}
