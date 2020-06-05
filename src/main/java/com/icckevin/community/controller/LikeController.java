package com.icckevin.community.controller;

import com.icckevin.community.entity.User;
import com.icckevin.community.service.LikeService;
import com.icckevin.community.utils.CommunityUtil;
import com.icckevin.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-06-05 08:23
 **/
@Controller

public class LikeController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId){
        User user = hostHolder.getUser();

        likeService.like(user.getId(),entityType,entityId);

        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeService.getLikeCount(entityType,entityId));
        map.put("likeStatus",likeService.getLikeStatus(user.getId(),entityType,entityId));

        return CommunityUtil.getJSONString(0,null,map);
    }
}