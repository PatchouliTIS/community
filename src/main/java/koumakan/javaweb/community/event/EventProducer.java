package koumakan.javaweb.community.event;

import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import koumakan.javaweb.community.entity.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @Package: koumakan.javaweb.community.event
 * @Author: Alice Maetra
 * @Date: 2023/5/8 19:40
 * @Decription:
 */

@Component
public class EventProducer {
    @Resource
    private KafkaTemplate kafkaTemplate;

    public void triggerEvent(Event event) {
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
