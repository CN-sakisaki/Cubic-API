package com.saki.apiproject.config;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.saki.apiproject.constant.CacheExpireConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine缓存配置类
 * 用于配置和初始化应用程序中的本地缓存实例
 * @author sakisaki
 * @date 2025/9/18 09:43
 */
@Configuration
public class CaffeineConfig {

    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 初始容量：缓存初始化时分配的空间大小，避免频繁扩容
                .initialCapacity(100)
                // 最大缓存数量：当缓存元素数量接近最大值时，会基于最近最少使用(LRU)算法移除缓存项
                .maximumSize(1000)
                .expireAfterWrite(CacheExpireConstant.CAFFEINE_CACHE_EXPIRE, TimeUnit.MINUTES)
                // 启用统计功能：收集缓存命中率、加载时间等统计信息，便于监控和优化
                .recordStats()
                .build();
    }
}
