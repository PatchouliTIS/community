package koumakan.javaweb.community.controller;

import com.alibaba.fastjson.JSONObject;
import koumakan.javaweb.community.entity.Message;
import koumakan.javaweb.community.entity.Page;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.MessageService;
import koumakan.javaweb.community.service.UserService;
import koumakan.javaweb.community.util.CommunityConstant;
import koumakan.javaweb.community.util.CommunityUtil;
import koumakan.javaweb.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import scala.Int;

import java.util.*;

/**
 * @Package: koumakan.javaweb.community.controller
 * @Author: Alice Maetra
 * @Date: 2023/3/29 16:30
 * @Decription:
 */

@Controller
public class MessageController implements CommunityConstant {


    @Autowired
    private HostHandler hostHandler;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;


    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHandler.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                // 如果当前遍历会话的 to_id 对象是自己，则加入已读列表。
                if (hostHandler.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }


    /**
     * 私信详情页面
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHandler.getUser();

        // 分页信息
        page.setLimit(5);
        page.setRows(messageService.findConversationCount(user.getId()));
        page.setPath("/letter/list");   // 相对于配置文件中 content-path的 路径

        // 会话列表
        List<Message> convList = messageService.findConversations(
                user.getId(), page.getOffset() ,page.getLimit());
        List<Map<String, Object>> convMapList = new ArrayList<>();
        if(convList != null) {
            for(Message m : convList) {
                Map<String, Object> map = new HashMap<>();
                // 准备前端 HTML 页面中需要动态显示的元素
                map.put("conversation", m);
                map.put("letterCount", messageService.findLetterCount(m.getConversationId()));
                // 当前对话未读消息数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), m.getConversationId()));
                int targetId = user.getId() == m.getFromId() ? m.getToId() : m.getFromId();
                map.put("target", userService.findUserById(targetId));

                convMapList.add(map);
            }
        }

        model.addAttribute("conversations", convMapList);

        // 查询全部对话未读消息数量
        int totalUnread = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", totalUnread);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }


    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if(target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在！");
        }

        Message message = new Message();
        message.setFromId(hostHandler.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }


    private Map<String, Object> getVO(String type, int userId) {
        Message message = messageService.findLatestNotice(userId, type);
        if(message == null) {
            return null;
        }
        Map<String, Object> msgVO = new HashMap<>();



        msgVO.put("message", message);

        String content = HtmlUtils.htmlUnescape(message.getContent());
        Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

        msgVO.put("user", userService.findUserById((Integer) data.get("userId")));
        msgVO.put("entityType", data.get("entityType"));
        msgVO.put("entityId", data.get("entityId"));
        msgVO.put("postId", data.get("postId"));


        int count = messageService.findNoticeCount(userId, type);
        msgVO.put("count", count);

        int unread = messageService.findNoticeUnreadCount(userId, type);
        msgVO.put("unread", unread);

        return msgVO;
    }

    @GetMapping(path = "/notice/list/")
    public String getNoticeList(Model model) {
        User user = hostHandler.getUser();


        // 查询评论
        Map<String, Object> commentVO = getVO(TOPIC_COMMENT, user.getId());
        model.addAttribute("commentNotice", commentVO);

        // 查询点赞
        Map<String, Object> likeVO = getVO(TOPIC_LIKE, user.getId());
        model.addAttribute("likeNotice", likeVO);

        // 查询关注
        Map<String, Object> followVO = getVO(TOPIC_FOLLOW, user.getId());
        model.addAttribute("followNotice", followVO);

        // 查询所有未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }



    @GetMapping(path = "/notice/detail/{topic}")
    public String getNoticeDetail(
            @PathVariable("topic") String topic,
            Page page,
            Model model) {
        User user = hostHandler.getUser();


        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        page.setPath("/notice/detail/" + topic);
        page.setLimit(5);

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());


        List<Map<String, Object>> noticeVOList = new ArrayList<>();

        for(Message m : noticeList) {
            Map<String, Object> map = new HashMap<>();
            // 通知
            map.put("notice", m);
            // 内容
            String content = HtmlUtils.htmlUnescape(m.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            map.put("user", userService.findUserById((Integer) data.get("userId")));
            map.put("entityType", data.get("entityType"));
            map.put("entityId", data.get("entityId"));
            map.put("postId", data.get("postId"));
            map.put("fromUser", userService.findUserById(m.getFromId()));


            noticeVOList.add(map);
        }

        model.addAttribute("notices", noticeVOList);


        // 设置已读消息
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()) {
            messageService.readMessage(ids);
        }


        return "/site/notice-detail";
    }

}
