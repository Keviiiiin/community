package com.icckevin.community.service;

import com.icckevin.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    // 涉及多次数据库操作，需要添加事务
    public void like(int userId,int entityType,int entityId,int entityUserId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);

                redisTemplate.multi();
                if(!isMember){
                    redisTemplate.opsForSet().add(entityLikeKey,userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }else {
                    redisTemplate.opsForSet().remove(entityLikeKey,userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                }
                return operations.exec();
            }
        });
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

    // 查询某用户收到的赞
    public long getUserLikeCount(int userId){
        String key = RedisKeyUtil.getUserLikeKey(userId);
        Long count = (Long)redisTemplate.opsForValue().get(key) ;
        return count == null ? 0 : count.longValue();
    }
}