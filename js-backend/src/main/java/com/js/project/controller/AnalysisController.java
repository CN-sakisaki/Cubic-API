package com.js.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.js.jsapicommon.model.entity.InterfaceInfo;
import com.js.jsapicommon.model.entity.UserInterfaceInfo;
import com.js.project.annotation.AuthCheck;
import com.js.project.common.BaseResponse;
import com.js.project.common.ErrorCode;
import com.js.project.common.ResultUtils;
import com.js.project.exception.BusinessException;
import com.js.project.job.InterfaceStatisticsTask;
import com.js.project.mapper.UserInterfaceInfoMapper;
import com.js.project.model.vo.InterfaceInfoVO;
import com.js.project.service.InterfaceInfoService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    /**
     * 获取调用次数最多的接口信息列表。
     * 通过用户接口信息表查询调用次数最多的接口ID，再关联查询接口详细信息。
     *
     * @return 接口信息列表，包含调用次数最多的接口信息
     */
    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        // 拿到缓存的结果
        Map<Long, InterfaceInfoVO> cachedMap = InterfaceStatisticsTask.getCachedInterfaceInfoVOMap();
        // 如果缓存不存在，就正常走数据库
        if (cachedMap.isEmpty()) {
            // 查询调用次数最多的接口信息列表
            List<UserInterfaceInfo> interfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(5);

            // 构建接口信息VO列表，使用流式处理将接口信息映射为接口信息VO对象，并加入列表中
            List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList.stream().map(userInterfaceInfo -> {
                // 创建一个新的接口信息VO对象
                InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
                // 将调用次数设置到接口信息VO对象中
                InterfaceInfo interfaceInfo = interfaceInfoService.getById(userInterfaceInfo.getInterfaceInfoId());

                interfaceInfoVO.setTotalNum(interfaceInfo.getTotalInvokes());
                interfaceInfoVO.setName(interfaceInfo.getName());
                interfaceInfoVO.setDescription(interfaceInfo.getDescription());
                // 返回构建好的接口信息VO对象
                return interfaceInfoVO;
            }).collect(Collectors.toList());
            // 返回处理结果
            return ResultUtils.success(interfaceInfoVOList);
        }
        List<InterfaceInfoVO> interfaceInfoVOList = new ArrayList<>(cachedMap.values());
        interfaceInfoVOList.sort(Comparator.comparingLong(InterfaceInfoVO::getTotalNum).reversed());
        return ResultUtils.success(interfaceInfoVOList);
    }
}
