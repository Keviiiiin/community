package com.icckevin.community.controller;

import com.icckevin.community.entity.DiscussPost;
import com.icckevin.community.entity.User;
import com.icckevin.community.service.DiscussPostService;
import com.icckevin.community.utils.CommunityUtil;
import com.icckevin.community.utils.HostHolder;
import com.icckevin.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @description: 处理帖子
 * @author: iccKevin
 * @create: 2020-05-26 16:42
 **/
@Controller
@RequestMapping(value = "/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(user == null)
            return CommunityUtil.getJSONString(403,"您还没有登录！");

        DiscussPost discussPost = new DiscussPost();

        discussPost.setUserId(user.getId());
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setCreateTime(new Date());

        discussPostService.insertDiscussPost(discussPost);

        return CommunityUtil.getJSONString(0,"发布成功！");
    }
}