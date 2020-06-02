package com.icckevin.community.controller;

import com.icckevin.community.dao.UserMapper;
import com.icckevin.community.entity.Message;
import com.icckevin.community.entity.Page;
import com.icckevin.community.entity.User;
import com.icckevin.community.service.MessageService;
import com.icckevin.community.service.UserService;
import com.icckevin.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-06-02 09:50
 **/
@RequestMapping(value = "/message",method = RequestMethod.GET)
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    // 获取用户
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(method = RequestMethod.GET)
    public String getMessage(Model model, Page page){
        int userId = hostHolder.getUser().getId();

        page.setLimit(5);
        page.setRows(messageService.selectMessageCount(userId));
        page.setPath("/message");

        List<Map<String,Object>> list = new ArrayList<>();
        List<Message> messages = messageService.selectMessage(userId, page.getStartRow(), page.getLimit());

        if(messages!=null) {
            for (Message message : messages) {
                // 注意：每次都要创建一个新的map，否则先插入的值会被后插入的修改
                Map<String,Object> map = new HashMap<>();
                // 查询与用户对话的人
                int selectId = message.getFromId() == userId ? message.getToId():message.getFromId();
                User target = userService.selectById(selectId);

                String conversationId = message.getConversationId();
                int unread = messageService.selectUnreadMessageCount(userId, conversationId);
                int total = messageService.selectConversationCount(conversationId);

                map.put("target", target);
                map.put("unreadConversation", unread);
                map.put("total", total);
                map.put("message", message);
                list.add(map);
            }
        }
        model.addAttribute("conversations",list);

        // 查询所有未读数量
        int unreadMessage = messageService.selectUnreadMessageCount(userId,null);
        model.addAttribute("unreadMessage",unreadMessage);
        return "/site/letter";
    }

    @RequestMapping("/detail/{conversationId}")
    public String getConversation(@PathVariable("conversationId") String conversationId,Model model,Page page){
        int userId = hostHolder.getUser().getId();

        page.setLimit(5);
        page.setPath("/message/detail/"+conversationId);
        page.setRows(messageService.selectConversationCount(conversationId));

        List<Map<String,Object>> list = new ArrayList<>();
        List<Message> messages = messageService.selectConversation(conversationId,page.getStartRow(),page.getLimit());

        if(messages!=null){
            for (Message message : messages) {
                Map<String,Object> map = new HashMap<>();

                int selectId = message.getFromId() == userId ? message.getToId():message.getFromId();
                User target = userService.selectById(selectId);

                map.put("target",target);
                map.put("message",message);

                list.add(map);
            }
        }
        model.addAttribute("conversations",list);
        return "/site/letter-detail";
    }
}