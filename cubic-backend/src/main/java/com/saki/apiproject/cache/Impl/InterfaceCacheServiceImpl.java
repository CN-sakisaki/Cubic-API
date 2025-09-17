package com.saki.apiproject.cache.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.saki.apiproject.cache.InterfaceCacheService;
import com.saki.apiproject.constant.CacheExpireConstant;
import com.saki.apiproject.constant.RedisKeyConstant;
import com.saki.apiproject.mapper.InterfaceInfoMapper;
import com.saki.common.model.entity.InterfaceInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 接口缓存实现类
 * @author sakisaki
 * @date 2025/9/17 22:18
 */
@Service
public class InterfaceCacheServiceImpl implements InterfaceCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final InterfaceInfoMapper interfaceInfoMapper;

    public InterfaceCacheServiceImpl(RedisTemplate<String, Object> redisTemplate, InterfaceInfoMapper interfaceInfoMapper) {
        this.redisTemplate = redisTemplate;
        this.interfaceInfoMapper = interfaceInfoMapper;
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

            // 缓存每个接口详情
            for (InterfaceInfo info : hotList) {
                redisTemplate.opsForValue().set(RedisKeyConstant.INTERFACE_INFO_PREFIX + info.getId(), info, CacheExpireConstant.INTERFACE_INFO_EXPIRE, TimeUnit.MINUTES);
            }
        }
    }

    /**
     * 获取接口详情（优先缓存）
     */
    @Override
    public InterfaceInfo getInterfaceInfoById(Long id) {
        String key = RedisKeyConstant.INTERFACE_INFO_PREFIX + id;
        InterfaceInfo info = (InterfaceInfo) redisTemplate.opsForValue().get(key);
        if (info != null) {
            return info;
        }
        // 缓存没有，查数据库并写入缓存
        info = interfaceInfoMapper.selectById(id);
        if (info != null) {
            redisTemplate.opsForValue().set(key, info, CacheExpireConstant.INTERFACE_INFO_EXPIRE, TimeUnit.MINUTES);
        }
        return info;
    }

    /**
     * 主动刷新缓存
     */
    @Override
    public void refreshInterfaceCache(Long id) {
        InterfaceInfo info = interfaceInfoMapper.selectById(id);
        if (info != null) {
            redisTemplate.opsForValue().set(RedisKeyConstant.INTERFACE_INFO_PREFIX + id, info, CacheExpireConstant.INTERFACE_INFO_EXPIRE, TimeUnit.MINUTES);
        }
    }
}
