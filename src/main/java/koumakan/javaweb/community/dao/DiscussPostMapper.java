package koumakan.javaweb.community.dao;

import koumakan.javaweb.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Package: koumakan.javaweb.community.dao
 * @Author: Alice Maetra
 * @Date: 2023/3/16 16:06
 * @Decription:
 *      论坛Mapper接口
 *      设置各种方法
 */

@Mapper
public interface DiscussPostMapper {

    /**
     *
     * @param userId
     * @param offset 用户所发帖占用的数据库页数
     * @param limit  每一页最大展示多少数据项
     * @return List 用户发帖的列表
     */
    List<DiscussPost> selectPosts(int userId, int offset, int limit);


    /**
     * 查询当前用户发帖总数
     * [@Param] 用于给参数起别名，如果当前方法只有一个参数
     *          且在Mapper映射器中<if></if>标签内使用
     *          这一参数，则必须取别名。
     * @param userId
     * @return int
     */
    int selectPostsCnt(@Param("userId")  int userId);


}
