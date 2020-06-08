package com.icckevin.community.service;

import com.icckevin.community.entity.User;
import com.icckevin.community.utils.EntityTypeConstant;
import com.icckevin.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-06-07 16:30
 **/
@Service
public class FollowService implements EntityTypeConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void follow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                redisTemplate.multi();

                redisTemplate.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                redisTemplate.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                return redisTemplate.exec();
            }
        });
    }

    /**
     * 取关
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void unfollow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                redisTemplate.multi();

                redisTemplate.opsForZSet().remove(followeeKey, entityId);
                redisTemplate.opsForZSet().remove(followerKey,userId);

                return redisTemplate.exec();
            }
        });
    }

    /**
     * 获取关注人数
     * @param userId
     * @param entityType
     * @return
     */
    public long getFolloweeCount(int userId,int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 获取粉丝人数
     * @param entityType
     * @param entityId
     * @return
     */
    public long getFollowerCount(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 查询当前用户是否已关注该实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    /**
     * 获取关注列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String,Object>> getFollowees(int userId,int offset,int limit){
        List<Map<String,Object>> list = new ArrayList<>();
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> followees = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if(followees != null){
            for (Integer id : followees) {
                Map<String,Object> map = new HashMap<>();
                double score = redisTemplate.opsForZSet().score(followeeKey, id);
                map.put("followTime",new Date((long)score));
                User user = userService.selectById(id);
                map.put("user",user);
                boolean hasFollowed = hasFollowed(userId,ENTITY_TYPE_USER,id);
                map.put("hasFollowed",hasFollowed);

                list.add(map);
            }
        }
        return list;
    }

    /**
     * 获取粉丝列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String,Object>> getFollowers(int userId,int offset,int limit){
        List<Map<String,Object>> list = new ArrayList<>();
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> followers = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(followers != null){
            for (Integer id : followers) {
                Map<String,Object> map = new HashMap<>();
                double score = redisTemplate.opsForZSet().score(followerKey, id);
                map.put("followTime",new Date((long)score));
                User user = userService.selectById(id);
                map.put("user",user);
                boolean hasFollowed = hasFollowed(userId,ENTITY_TYPE_USER,id);
                map.put("hasFollowed",hasFollowed);

                list.add(map);
            }
        }
        return list;
    }
}