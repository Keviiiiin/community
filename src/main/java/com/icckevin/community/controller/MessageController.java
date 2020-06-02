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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/{userId}",method = RequestMethod.GET)
    public String getMessage(@PathVariable("userId")int userId, Model model, Page page){

        page.setRows(messageService.selectMessageCount(userId));
        page.setPath("/message");

        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        List<Message> messages = messageService.selectMessage(userId, 0, page.getLimit());
        for (Message message : messages) {
            int fromId = message.getFromId();
            User user = userService.selectById(fromId);

            String conversationId = Math.min(fromId,userId) + "_" + Math.max(fromId,userId);
            int unread = messageService.selectUnreadMessageCount(userId,conversationId);

            map.put("user",user);
            map.put("message",message);
            map.put("unread",unread);
            list.add(map);
        }
        model.addAttribute("list",list);
        return "/site/letter";
    }

    @RequestMapping("/detail")
    public String getConversation(@PathVariable("userId")int userId,Model model,Page page){
        page.setRows(messageService.selectConversationCount());
    }
}