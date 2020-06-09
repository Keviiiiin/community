package com.icckevin.community.utils;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-06-05 07:46
 **/
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId1,userId2,...)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户收到的赞
    // like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体
    // followee:userId:entityType -> zset(entityId,time) 用当前时间充当zset的分数
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,time)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType+ SPLIT + entityId;
    }

    public static String getKaptcha(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }
}