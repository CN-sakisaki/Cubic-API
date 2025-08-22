package com.saki.apiproject.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;


import com.saki.apiproject.mapper.UserInterfaceInfoMapper;
import com.saki.apiproject.model.vo.InterfaceInfoVO;
import com.saki.apiproject.service.InterfaceInfoService;
import com.saki.common.model.entity.InterfaceInfo;
import com.saki.common.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InterfaceStatisticsTask {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    // 使用 ConcurrentHashMap 存储缓存数据
    private static final Map<Long, InterfaceInfoVO> cachedInterfaceInfoVOMap = new ConcurrentHashMap<>();

    /**
     * 获取缓存数据
     *
     * @return 缓存数据
     */
    public static Map<Long, InterfaceInfoVO> getCachedInterfaceInfoVOMap() {
        return Collections.unmodifiableMap(cachedInterfaceInfoVOMap);
    }

    /**
     * 每天凌晨2点执行任务
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void calculateTopInvokeInterfaceInfo() {
        log.info("开始执行接口调用统计任务...");

        try {
            // 获取调用次数最多的接口信息列表
            List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(5);
            if (CollectionUtils.isEmpty(userInterfaceInfoList)) {
                log.info("没有找到接口调用数据");
                return;
            }

            // 按接口ID分组
            Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream()
                    .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));

            // 查询接口信息
            List<InterfaceInfo> interfaceInfoList = queryInterfaceInfoByIds(interfaceInfoIdObjMap.keySet());
            if (CollectionUtils.isEmpty(interfaceInfoList)) {
                log.info("没有找到对应的接口信息");
                return;
            }

            // 构建 InterfaceInfoVO 列表
            List<InterfaceInfoVO> interfaceInfoVOList = buildInterfaceInfoVOList(interfaceInfoList, interfaceInfoIdObjMap);

            // 更新缓存
            updateCache(interfaceInfoVOList);
            log.info("接口调用统计任务执行完成，更新缓存成功");
        } catch (Exception e) {
            log.error("接口调用统计任务执行失败", e);
        }
    }

    /**
     * 根据接口ID列表查询接口信息
     *
     * @param interfaceInfoIds 接口ID列表
     * @return 接口信息列表
     */
    private List<InterfaceInfo> queryInterfaceInfoByIds(Set<Long> interfaceInfoIds) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", interfaceInfoIds);
        return interfaceInfoService.list(queryWrapper);
    }

    /**
     * 构建 InterfaceInfoVO 列表
     *
     * @param interfaceInfoList      接口信息列表
     * @param interfaceInfoIdObjMap  接口调用数据分组
     * @return InterfaceInfoVO 列表
     */
    private List<InterfaceInfoVO> buildInterfaceInfoVOList(List<InterfaceInfo> interfaceInfoList,
                                                           Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap) {
        return interfaceInfoList.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            // 使用手动赋值代替 BeanUtils.copyProperties
            interfaceInfoVO.setId(interfaceInfo.getId());
            interfaceInfoVO.setName(interfaceInfo.getName());
            interfaceInfoVO.setDescription(interfaceInfo.getDescription());
            // 其他属性赋值...

            // 设置总调用次数
            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVO.setTotalNum(totalNum);
            return interfaceInfoVO;
        }).collect(Collectors.toList());
    }

    /**
     * 更新缓存
     *
     * @param interfaceInfoVOList InterfaceInfoVO 列表
     */
    private void updateCache(List<InterfaceInfoVO> interfaceInfoVOList) {
        Map<Long, InterfaceInfoVO> newCache = new ConcurrentHashMap<>();
        interfaceInfoVOList.forEach(vo -> newCache.put(vo.getId(), vo));

        // 使用 putAll 一次性更新缓存，避免短暂的不一致问题
        cachedInterfaceInfoVOMap.clear();
        cachedInterfaceInfoVOMap.putAll(newCache);
    }
}