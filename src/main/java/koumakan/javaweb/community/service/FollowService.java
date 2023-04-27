package koumakan.javaweb.community.service;

import jakarta.annotation.Resource;
import koumakan.javaweb.community.util.CommunityConstant;
import koumakan.javaweb.community.util.RedisKeyUtil;
import org.apache.ibatis.ognl.ObjectElementsAccessor;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Package: koumakan.javaweb.community.service
 * @Author: Alice Maetra
 * @Date: 2023/4/27 14:28
 * @Decription:
 */
@Service
public class FollowService implements CommunityConstant {

    @Resource
    private RedisTemplate<String, Integer> redisTemplate;

    @Resource
    private UserService userService;

    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                // 某个实体具有多少个粉丝
                String follower = RedisKeyUtil.getFollowerKey(entityType, entityId);

                // 某个用户所关注的某个种类的实体
                String followee = RedisKeyUtil.getFolloweeKey(userId, entityType);

                operations.multi();

                BoundZSetOperations<String, Integer> followeeOPS = redisTemplate.boundZSetOps(followee);
                BoundZSetOperations<String, Integer> followerOPS = redisTemplate.boundZSetOps(follower);


                followerOPS.add(userId, System.currentTimeMillis());
                followeeOPS.add(entityId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }


    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                // 某个实体具有多少个粉丝
                String follower = RedisKeyUtil.getFollowerKey(entityType, entityId);

                // 某个用户所关注的实体
                String followee = RedisKeyUtil.getFolloweeKey(userId, entityType);

                operations.multi();

                BoundZSetOperations<String, Integer> followeeOPS = redisTemplate.boundZSetOps(followee);
                BoundZSetOperations<String, Integer> followerOPS = redisTemplate.boundZSetOps(follower);


                followerOPS.remove(userId);
                followeeOPS.remove(entityId);

                return operations.exec();
            }
        });
    }


    /**
     * 获取当前用户关注某一类实体的个数
     * @param userId
     * @param entityType
     * @return
     */
    public long findFolloweeCount(int userId, int entityType) {
        String followee = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followee);
    }

    /**
     * 获取当前实体的粉丝数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findFollowerCount(int entityType, int entityId) {
        String follower = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(follower);
    }

    /**
     * 查询当前登录的用户是否已关注当前个人资料界面的用户
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollowed(int userId, int entityType, int entityId) {
        String followee = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followee, entityId) != null;
    }


    /**
     * 查询某个用户的关注用户列表
     * 其他关注实体类型可以通过ENTITY_TYPE_XXX更改
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followee = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);

        /**
         * zset中的数据是根据score也就是时间的大小从小到大排序，在前面的是最早发生的事务。
         * 因此需要倒序遍历获取最新的关注列表。
         */
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followee, offset, offset + limit - 1);

        List<Map<String, Object>> followeesLists = new ArrayList<Map<String, Object>>();
        assert targetIds != null;
        for(Integer id : targetIds) {
            Map<String, Object> followeeMap = new HashMap<>();
            followeeMap.put("user",userService.findUserById(id));
            Double score = redisTemplate.opsForZSet().score(followee, id);
            assert score != null;
            followeeMap.put("followTime", new Date(score.longValue()));
            followeesLists.add(followeeMap);
        }

        return followeesLists;
    }


    /**
     * 查询当前用户的粉丝列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String follower = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(follower, offset, offset + limit - 1);

        List<Map<String, Object>> lists = new ArrayList<>();

        assert targetIds != null;
        for(Integer id : targetIds) {
            Map<String, Object> map = new HashMap<>();

            map.put("user", userService.findUserById(id));

            Double time = redisTemplate.opsForZSet().score(follower, id);

            assert time != null;
            map.put("followTime", new Date(time.longValue()));

            lists.add(map);
        }

        return lists;
    }


}
