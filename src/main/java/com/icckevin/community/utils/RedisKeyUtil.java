package com.icckevin.community.utils;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-06-05 07:46
 **/
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
}