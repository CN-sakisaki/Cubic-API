package com.js.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.js.jsapicommon.model.entity.InterfaceInfo;
import com.js.jsapicommon.service.InnerInterfaceInfoService;
import com.js.project.common.ErrorCode;
import com.js.project.exception.BusinessException;
import com.js.project.mapper.InterfaceInfoMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 内部接口服务实现类
 * @date 2024-09-13 12:10:17
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请检查请求的参数");
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", url);
        queryWrapper.eq("method", method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }

}
