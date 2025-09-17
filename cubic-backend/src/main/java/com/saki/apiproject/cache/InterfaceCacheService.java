package com.saki.apiproject.cache;


import com.saki.common.model.entity.InterfaceInfo;

/**
 * 接口信息缓存 接口
 * @author sakisaki
 * @date 2025/9/17 21:52
 */
public interface InterfaceCacheService {
    /**
     * 缓存预热
     */
    void preheatHotInterfaces();

    /**
     * 获取接口详情（带缓存）
     */
    InterfaceInfo getInterfaceInfoById(Long id);

    /**
     * 刷新某个接口缓存
     */
    void refreshInterfaceCache(Long id);
}
