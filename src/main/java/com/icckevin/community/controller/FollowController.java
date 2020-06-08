package com.icckevin.community.controller;

import com.icckevin.community.entity.Page;
import com.icckevin.community.entity.User;
import com.icckevin.community.service.FollowService;
import com.icckevin.community.service.UserService;
import com.icckevin.community.utils.CommunityUtil;
import com.icckevin.community.utils.EntityTypeConstant;
import com.icckevin.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-06-07 17:27
 **/
@Controller
public class FollowController implements EntityTypeConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已关注！");
    }
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已取消关注!");
    }

    @RequestMapping(value = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable int userId, Model model, Page page){

        User user = userService.selectById(userId);
        if(user == null)
            throw new RuntimeException("该用户不存在!");
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int)followService.getFolloweeCount(userId,ENTITY_TYPE_USER));

        List<Map<String, Object>> followees = followService.getFollowees(userId, page.getStartRow(), page.getLimit());
        model.addAttribute("followees",followees);

        return "/site/followee";
    }

    @RequestMapping(value = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable int userId, Model model, Page page){

        User user = userService.selectById(userId);
        if(user == null)
            throw new RuntimeException("该用户不存在!");
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int)followService.getFollowerCount(ENTITY_TYPE_USER,userId));

        List<Map<String, Object>> followers = followService.getFollowers(userId, page.getStartRow(), page.getLimit());
        model.addAttribute("followers",followers);

        return "/site/follower";
    }
}