package koumakan.javaweb.community.dao;

import koumakan.javaweb.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Package: koumakan.javaweb.community.dao
 * @Author: Alice Maetra
 * @Date: 2023/3/29 16:03
 * @Decription:
 */
@Mapper
public interface MessageMapper {

    /**
     * 查询 userId 对应用户的当前对话，只显示每个对话的最新一条消息
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 获取当前用户的对话总数，用于分页。
     * @param userId
     * @return
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个对话的所有消息
     * @param convId 对话的 ID ， 对应数据库中 conversation_id
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectLetters(String convId, int offset, int limit);

    int selectLetterCount(String convId);

    /**
     * 获取对话页面中显示的各个对话的未读消息数目
     * @param userId
     * @param convId
     * @return
     */
    int selectNewLetterCount(int userId, String convId);

    /**
     * 新增消息
     * @param message
     * @return
     */
    int insertMessage(Message message);

    /**
     * 修改消息列表状态 已读：1  未读：0
     * @param ids
     * @param status
     * @return
     */
    int updateStatus(List<Integer> ids, int status);

}
