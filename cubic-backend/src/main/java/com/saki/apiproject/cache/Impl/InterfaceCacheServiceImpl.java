package com.saki.apiproject.cache.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.saki.apiproject.cache.InterfaceCacheService;
import com.saki.apiproject.constant.CacheExpireConstant;
import com.saki.apiproject.constant.RedisKeyConstant;
import com.saki.apiproject.mapper.InterfaceInfoMapper;
import com.saki.common.model.entity.InterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 接口缓存实现类
 * @author sakisaki
 * @date 2025/9/17 22:18
 */
@Slf4j
@Service
public class InterfaceCacheServiceImpl implements InterfaceCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final InterfaceInfoMapper interfaceInfoMapper;

    private final Cache<String, Object> caffeineCache;

    public InterfaceCacheServiceImpl(RedisTemplate<String, Object> redisTemplate, InterfaceInfoMapper interfaceInfoMapper, Cache<String, Object> caffeineCache) {
        this.redisTemplate = redisTemplate;
        this.interfaceInfoMapper = interfaceInfoMapper;
        this.caffeineCache = caffeineCache;
    }

    /**
     * 缓存预热 - 启动时执行
     */
    @Override
    public void preheatHotInterfaces() {
        // 查询调用次数最多的前三个接口
        List<InterfaceInfo> hotList = interfaceInfoMapper.selectList(new QueryWrapper<InterfaceInfo>().orderByDesc("totalInvokes").last("limit 3"));

        if (hotList != null && !hotList.isEmpty()) {
            // 缓存热点列表
            redisTemplate.opsForValue().set(RedisKeyConstant.INTERFACE_HOT_LIST, hotList, CacheExpireConstant.INTERFACE_HOT_LIST_EXPIRE, TimeUnit.MINUTES);

            // Caffeine 缓存热点列表
            caffeineCache.put(RedisKeyConstant.INTERFACE_HOT_LIST, hotList);

            // 缓存每个接口详情（Redis + Caffeine）
            for (InterfaceInfo info : hotList) {
                String key = RedisKeyConstant.INTERFACE_INFO_PREFIX + info.getId();
                redisTemplate.opsForValue().set(key, info, CacheExpireConstant.INTERFACE_INFO_EXPIRE, TimeUnit.MINUTES);
                caffeineCache.put(key, info);
            }
        }
    }

    /**
     * 获取接口详情（优先缓存）
     * @param id 接口ID
     * @return 接口信息
     */
    @Override
    public InterfaceInfo getInterfaceInfoById(Long id) {
        String key = RedisKeyConstant.INTERFACE_INFO_PREFIX + id;

        // 1. 查Caffeine
        InterfaceInfo info = (InterfaceInfo) caffeineCache.getIfPresent(key);
        if (info != null) {
            log.info("执行了Caffeine");
            return info;
        }

        // 2. 查Redis
        info = (InterfaceInfo) redisTemplate.opsForValue().get(key);
        if (info != null) {
            log.info("查询走Redis");
            return info;
        }
        // 3. 缓存没有，查数据库并写入缓存
        info = interfaceInfoMapper.selectById(id);
        if (info != null) {
            log.info("写入缓存");
            redisTemplate.opsForValue().set(key, info, CacheExpireConstant.INTERFACE_INFO_EXPIRE, TimeUnit.MINUTES);
            caffeineCache.put(key, info);
        }
        return info;
    }

    /**
     * 主动刷新缓存
     * @param id 接口ID
     */
    @Override
    public void refreshInterfaceCache(Long id) {
        InterfaceInfo info = interfaceInfoMapper.selectById(id);
        if (info != null) {
            String key = RedisKeyConstant.INTERFACE_INFO_PREFIX + id;
            redisTemplate.opsForValue().set(key, info, CacheExpireConstant.INTERFACE_INFO_EXPIRE, TimeUnit.MINUTES);
            caffeineCache.put(key, info);
        }
    }
}
