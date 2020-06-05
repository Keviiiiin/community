package com.icckevin.community.service;

import com.icckevin.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-06-05 07:38
 **/
@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞/取消赞
    public void like(int userId,int entityType,int entityId){
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        boolean isMember = redisTemplate.opsForSet().isMember(key,userId);
        if(!isMember){
            redisTemplate.opsForSet().add(key,userId);
        }else {
            redisTemplate.opsForSet().remove(key,userId);
        }
    }

    // 查询点赞数量
    public long getLikeCount(int entityType,int entityId) {
        return redisTemplate.opsForSet().size(RedisKeyUtil.getEntityLikeKey(entityType, entityId));
    }

    // 查询某人对实体的点赞状态
    public int getLikeStatus(int userId,int entityType,int entityId){
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return  redisTemplate.opsForSet().isMember(key,userId)?1:0;
    }
}