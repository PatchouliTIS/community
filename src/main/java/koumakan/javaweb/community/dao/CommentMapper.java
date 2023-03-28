package koumakan.javaweb.community.dao;

import koumakan.javaweb.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Package: koumakan.javaweb.community.dao
 * @Author: Alice Maetra
 * @Date: 2023/3/28 14:02
 * @Decription:
 */

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit) ;

    int selectCountByEntity(int entityType, int entityId);


    int insertComment(Comment comment);

}
