package com.saki.apiproject.constant;

/**
 * 缓存过期时间常量
 * @author sakisaki
 * @date 2025/9/17 22:22
 */
public interface CacheExpireConstant {

    /**
     * 热点接口列表缓存过期时间（30分钟）
     */
    long INTERFACE_HOT_LIST_EXPIRE = 30L;

    /**
     * 热点接口缓存过期时间（60 分钟）
     */
    long INTERFACE_INFO_EXPIRE = 10L;

    /**
     * Caffeine 接口详细信息缓存过期时间
     */
    long CAFFEINE_CACHE_EXPIRE = 5L;
}
