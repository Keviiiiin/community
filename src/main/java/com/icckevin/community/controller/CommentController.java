package com.icckevin.community.controller;

import com.icckevin.community.entity.Comment;
import com.icckevin.community.service.CommentService;
import com.icckevin.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @description: 帖子详情页面的添加评论/回复功能
 * @author: iccKevin
 * @create: 2020-05-31 11:43
 **/
@RequestMapping("/comment")
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.insertComment(comment);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}