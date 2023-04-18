package koumakan.javaweb.community.service;

import jakarta.annotation.Resource;
import koumakan.javaweb.community.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

/**
 * @Package: koumakan.javaweb.community.service
 * @Author: Alice Maetra
 * @Date: 2023/4/18 9:07
 * @Decription:
 */

@Service
public class LikeService {

    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 点赞功能
     * 使用 Transaction 保证一致性
     * 同时对用户（实体拥有者）点赞和实体点赞的更新
     * @param userId
     * @param entityType
     * @param entityId
     * @param entityUserId 为了避免在使用Redis时的同时又查询数据库影响效率，让前端直接返回entity的拥有者userId
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);


                operations.multi();

                if(isMember == true) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });
    }

    /**
     * 每个帖子实体点赞的统计
     * @param entityType
     * @param entityId
     * @return 点赞总数
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        BoundSetOperations bso = redisTemplate.boundSetOps(entityLikeKey);

        return (long)bso.size();
    }


    /**
     * 查询用户对帖子的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return 返回布尔值，用于后续点踩的功能开发
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        BoundSetOperations bso = redisTemplate.boundSetOps(entityLikeKey);

        return Boolean.TRUE.equals(bso.isMember(userId)) == true ? 1 : 0;

    }



    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        BoundValueOperations bso = redisTemplate.boundValueOps(userLikeKey);

        Integer count = (Integer)bso.get();

        return count == null ? 0 : count.intValue();
    }

}
