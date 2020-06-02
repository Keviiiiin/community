package com.icckevin.community.service;

import com.icckevin.community.dao.MessageMapper;
import com.icckevin.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-06-02 09:45
 **/
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    public List<Message> selectMessage(int userId, int offset, int limit){
        return messageMapper.selectMessage(userId,offset,limit);
    }

    public int selectMessageCount(int userId){
        return messageMapper.selectMessageCount(userId);
    }

    public List<Message> selectConversation(String conversationId,int offset,int limit){
        return messageMapper.selectConversation(conversationId,offset,limit);
    }

    public int selectConversationCount(String conversationId){
        return messageMapper.selectConversationCount(conversationId);
    }

    public int selectUnreadMessageCount(int userId,String conversationId){
        return messageMapper.selectUnreadMessageCount(userId,conversationId);
    }
}