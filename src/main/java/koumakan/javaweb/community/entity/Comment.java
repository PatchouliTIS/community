package koumakan.javaweb.community.entity;

import java.util.Date;

/**
 * @Package: koumakan.javaweb.community.entity
 * @Author: Alice Maetra
 * @Date: 2023/3/28 13:56
 * @Decription:
 */
public class Comment {
    private int id;
    private int userId;

    /**
     * entity_type 标识回帖的类型，与entity_id相关联
     * 如果entity_type为1，则表明本comment是对帖子的评论，entity_id对应着discuss_post中的id，也就是帖子ID；
     * 如果entity_type为2，则表明本comment是对帖子中评论的评论，entity_id对应着comment中的id，也就是评论ID，
     *                                                   此时 target_id 可能会大于0，如果大于0则表明是楼中楼
     *
     * cnm屎一样的数据库表设计
     */
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
