package com.icckevin.community.controller;

import com.icckevin.community.entity.Comment;
import com.icckevin.community.entity.DiscussPost;
import com.icckevin.community.entity.Page;
import com.icckevin.community.entity.User;
import com.icckevin.community.service.CommentService;
import com.icckevin.community.service.DiscussPostService;
import com.icckevin.community.service.UserService;
import com.icckevin.community.utils.CommunityUtil;
import com.icckevin.community.utils.EntityTypeConstant;
import com.icckevin.community.utils.HostHolder;
import com.icckevin.community.utils.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @description: 处理帖子
 * @author: iccKevin
 * @create: 2020-05-26 16:42
 **/
@Controller
@RequestMapping(value = "/discuss")
public class DiscussPostController implements EntityTypeConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private HostHolder hostHolder;

    //发布一条帖子
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(user == null)
            return CommunityUtil.getJSONString(403,"您还没有登录！");

        if(StringUtils.isBlank(title) || StringUtils.isBlank(content))
            return CommunityUtil.getJSONString(404,"请输入有效的字符！");

        DiscussPost discussPost = new DiscussPost();

        discussPost.setUserId(user.getId());
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setCreateTime(new Date());

        discussPostService.insertDiscussPost(discussPost);

        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    @RequestMapping(value = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        // 帖子
        DiscussPost discussPost = discussPostService.selectDiscussPostById(discussPostId);
        model.addAttribute("post",discussPost);

        // 作者
        User user = userService.selectById(discussPost.getUserId());
        model.addAttribute("user",user);

        page.setLimit(5);
        // 总行数直接使用当前帖子的评论数量属性
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());

        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, discussPost.getId(), page.getStartRow(), page.getLimit());
        // 评论VO列表，封装了评论、用户、回复、回复数量，即[{comment=...,user=...,reply=...,count=...},{...},...]
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.selectById(comment.getUserId()));

                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.selectById(reply.getUserId()));
                        // 回复目标，判断是普通回复还是给特定用户的回复
                        User target = reply.getTargetId() == 0 ? null : userService.selectById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
//        System.out.println(commentVoList);
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}