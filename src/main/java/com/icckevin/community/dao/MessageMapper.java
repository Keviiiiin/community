package com.icckevin.community.dao;

import com.icckevin.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * 查询某个用户的私信列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectMessage(int userId,int offset,int limit);

    /**
     * 查询某用户私信总数
     * @param userId
     * @return
     */
    int selectMessageCount(int userId);

    /**
     * 查询私信详情
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectConversation(String conversationId,int offset,int limit);

    /**
     * 查询会话总数
     * @param conversationId
     * @return
     */
    int selectConversationCount(String conversationId);

    /**
     * 查询未读私信/会话总数，conversationId为空时表示查询未读私信
     * @param userId
     * @param conversationId
     * @return
     */
    int selectUnreadMessageCount(int userId,String conversationId);

    /**
     * 新增消息
     * @param message
     * @return
     */
    int insertMessage(Message message);

    /**
     * 修改消息状态（已读、未读）
     * @param ids
     * @param status
     * @return
     */
    int updateStatus(List<Integer> ids,int status);
}
