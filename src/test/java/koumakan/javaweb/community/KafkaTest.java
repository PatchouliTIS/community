package koumakan.javaweb.community;

import jakarta.annotation.Resource;
import jakarta.annotation.Resources;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

/**
 * @Package: koumakan.javaweb.community
 * @Author: Alice Maetra
 * @Date: 2023/5/7 17:23
 * @Decription:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {

    @Resource
    private Sender sender;

    @Test
    public void testKafkaQueue() {
        sender.send("test", "田所浩二");
        sender.send("test", "YSJE");


        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


@Component
class Sender {

    @Resource
    private KafkaTemplate kafkaTemplate;

    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}

@Component
class Consumer {

    @KafkaListener(topics={"test"})
    public void get(ConsumerRecord cr) {
        System.out.println("114514:" + cr.value());
    }
}
