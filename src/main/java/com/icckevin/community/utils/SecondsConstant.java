package com.icckevin.community.utils;

/**
 * 登录状态超时时间
 */
public interface SecondsConstant {
    // "记住"状态的超时时间：100天
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    // 默认状态过期时间：12小时
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
}
