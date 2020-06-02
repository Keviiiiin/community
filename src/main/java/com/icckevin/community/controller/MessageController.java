package com.icckevin.community.controller;

import com.icckevin.community.dao.UserMapper;
import com.icckevin.community.entity.Message;
import com.icckevin.community.entity.Page;
import com.icckevin.community.entity.User;
import com.icckevin.community.service.MessageService;
import com.icckevin.community.service.UserService;
import com.icckevin.community.utils.CommunityUtil;
import com.icckevin.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-06-02 09:50
 **/
@RequestMapping(value = "/message")
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

    @RequestMapping(value = "/detail/{conversationId}",method = RequestMethod.GET)
    public String getConversation(@PathVariable("conversationId") String conversationId,Model model,Page page){
        int userId = hostHolder.getUser().getId();

        page.setLimit(5);
        page.setPath("/message/detail/"+conversationId);
        page.setRows(messageService.selectConversationCount(conversationId));

        List<Map<String,Object>> list = new ArrayList<>();
        List<Message> messages = messageService.selectConversation(conversationId,page.getStartRow(),page.getLimit());
        List<Integer> ids = new ArrayList<>();

        if(messages!=null){
            for (Message message : messages) {
                Map<String,Object> map = new HashMap<>();

                map.put("fromUser",userService.selectById(message.getFromId()));
                map.put("message",message);

                list.add(map);

                // 找出私信中的未读私信
                if(userId == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        model.addAttribute("letters",list);

        //只有一个targetId,我们可以从conversationId中截取
        String[] s = conversationId.split("_");
        String tId = s[0].equals(userId+"") ? s[1] : s[0];
        int targetId = Integer.parseInt(tId);
        User target = userService.selectById(targetId);

        model.addAttribute("target",target);

        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    @RequestMapping(value = "/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toName,String content){
        User targetUser = userService.selectByName(toName);
        if(targetUser == null){
            return CommunityUtil.getJSONString(0,"该用户不存在!");
        }

        Message message = new Message();
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(targetUser.getId());
        String conversationId = Math.min(message.getFromId(),message.getToId()) + "_"
                + Math.max(message.getFromId(),message.getToId());
        message.setConversationId(conversationId);
        messageService.sendMessage(message);

        return CommunityUtil.getJSONString(1);
    }
}