package com.saki.apiproject.job;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.saki.apiproject.mapper.InterfaceInfoMapper;
import com.saki.apiproject.model.vo.InterfaceInvokeCountVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接口统计任务，用于定时计算并缓存接口调用次数最多的前几个接口
 *
 * @author sakisaki
 * @date 2025/9/17 23:33
 */
@Component
@Slf4j
public class InterfaceStatisticsTask {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * 使用 ConcurrentHashMap 存储缓存数据，键为接口ID，值为接口调用统计VO，存储在 JVM 的方法区
     */
    private static final Map<Long, InterfaceInvokeCountVO> cachedInterfaceInfoVOMap = new ConcurrentHashMap<>();

    /**
     * 获取缓存数据
     *
     * @return 缓存数据的不可修改视图
     */
    public static Map<Long, InterfaceInvokeCountVO> getCachedInterfaceInfoVOMap() {
        return Collections.unmodifiableMap(cachedInterfaceInfoVOMap);
    }

    /**
     * 每天凌晨2点执行任务，统计接口调用次数并更新缓存
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void calculateTopInvokeInterfaceInfo() {
        log.info("开始执行接口调用统计任务...");
        try {
            // 从数据库查询前5个调用次数最多的接口
            List<InterfaceInvokeCountVO> topList = interfaceInfoMapper.listTopInvokeInterfaceInfo(5);
            if (CollectionUtils.isEmpty(topList)) {
                log.info("没有找到接口调用数据");
                return;
            }
            // 更新缓存
            updateCache(topList);
            log.info("接口调用统计任务执行完成，更新缓存成功");
        } catch (Exception e) {
            log.error("接口调用统计任务执行失败", e);
        }
    }

    /**
     * 更新缓存：先将新的数据放入临时Map，然后一次性替换原有缓存，以减少锁的竞争和保证数据一致性
     *
     * @param topList interfaceInvokeCountVO 列表
     */
    private void updateCache(List<InterfaceInvokeCountVO> topList) {
        Map<Long, InterfaceInvokeCountVO> newCache = new ConcurrentHashMap<>();
        topList.forEach(vo -> newCache.put(vo.getId(), vo));

        // 清空原缓存并放入新数据（注意：此处是静态变量，需要考虑线程安全，但ConcurrentHashMap的putAll是安全的）
        cachedInterfaceInfoVOMap.clear();
        cachedInterfaceInfoVOMap.putAll(newCache);
    }
}