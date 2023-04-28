package koumakan.javaweb.community.util;

import javax.swing.plaf.SplitPaneUI;

/**
 * @Package: koumakan.javaweb.community.util
 * @Author: Alice Maetra
 * @Date: 2023/4/18 9:03
 * @Decription:
 *      保存RedisKey的工具类，直接设置成静态方法，通过类名调用
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";


    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    private static final String PREFIX_KPATCHA = "kaptcha";

    private static final String PREFIX_TICKET = "ticket";

    private static final String PREFIX_USER = "user";

    /**
     * 对某个实体的赞的更新
     * like:entity:entityType:entityId -> set(userId)
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }


    /**
     * 查询某个用户的赞
     * 在Redis中以userId为Key，其获得点赞数为Value
     * @param userId
     * @return
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }


    /**
     * 某个用户关注的实体  followee表示被关注的目标
     * 这样设置RedisKey可以区分当前用户关注实体的类型，用户关注帖子一类，用户关注用户一类... etc.
     * Redis中的数据结构：
     *      followee:userId:entityType -> zset(entityId, date)
     * @param userId
     * @param entityType
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }


    /**
     * 某实体的粉丝  follower表示关注者
     * Redis中的数据结构：
     *      followee:entityType:entityId -> zset(userId, date)
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }


    public static String getKaptchaKey (String owner) {
        return PREFIX_KPATCHA + SPLIT + owner;
    }

    public static String getTicketKey (String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }


    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
