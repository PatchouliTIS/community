package koumakan.javaweb.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: koumakan.javaweb.community.entity
 * @Author: Alice Maetra
 * @Date: 2023/5/8 19:27
 * @Decription:
 */
public class Event {
    private String topic;
    private int userId;
    private int entityType;
    private int entityId;
    private int entityUserId;
    private Map<String, Object> data;

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        if(this.data == null) {
            synchronized (this.getClass()) {
                if(this.data == null) {
                    this.data = new HashMap<>();
                }
            }
        }

        this.data.put(key, value);

        return this;

    }
}
