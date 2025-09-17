package com.saki.apiproject.controller;

import com.saki.apiproject.annotation.AuthCheck;
import com.saki.apiproject.job.InterfaceStatisticsTask;
import com.saki.apiproject.mapper.InterfaceInfoMapper;
import com.saki.apiproject.model.vo.InterfaceInvokeCountVO;
import com.saki.common.common.BaseResponse;
import com.saki.common.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 分析控制器
 * @date 2024-09-13 12:03:39
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInvokeCountVO>> listTopInvokeInterfaceInfo() {
        // 从定时任务缓存中获取接口调用统计信息
        Map<Long, InterfaceInvokeCountVO> cachedMap = InterfaceStatisticsTask.getCachedInterfaceInfoVOMap();
        // 如果缓存为空，则从数据库查询
        if (cachedMap.isEmpty()) {
            List<InterfaceInvokeCountVO> list = interfaceInfoMapper.listTopInvokeInterfaceInfo(5);
            return ResultUtils.success(list);
        }
        // 将缓存中的值转换为列表，并按总调用次数降序排序
        List<InterfaceInvokeCountVO> list = new ArrayList<>(cachedMap.values());
        list.sort(Comparator.comparingLong(InterfaceInvokeCountVO::getTotalInvokes).reversed());
        return ResultUtils.success(list);
    }
}
