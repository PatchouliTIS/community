package koumakan.javaweb.community.event;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.function.ObjBoolConsumer;
import jakarta.annotation.Resource;
import koumakan.javaweb.community.entity.Event;
import koumakan.javaweb.community.entity.Message;
import koumakan.javaweb.community.service.MessageService;
import koumakan.javaweb.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package: koumakan.javaweb.community.event
 * @Author: Alice Maetra
 * @Date: 2023/5/8 19:38
 * @Decription:
 */

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    @Resource
    private MessageService messageService;

    @KafkaListener(topics={TOPIC_COMMENT,TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleEvent(ConsumerRecord cr) {
        if(cr == null || cr.value() == null) {
            LOGGER.error("KAFKA系统消息-->参数为空！");
            return;
        }

        // 获取JSON格式的Event类实例对象并进行转化
        Event event = JSONObject.parseObject(cr.value().toString(), Event.class);
        if(event == null) {
            LOGGER.error("KAFKA系统消息-->格式错误，无法读取！");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        message.setToId(event.getEntityUserId());

        // 将event中的内容整合成JSONString的格式
        Map<String, Object> content = new HashMap<>();

        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if(event.getData() != null) {
            for(Map.Entry<String, Object> e : event.getData().entrySet()) {
                content.put(e.getKey(), e.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);

    }




}
