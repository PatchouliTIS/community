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
}
