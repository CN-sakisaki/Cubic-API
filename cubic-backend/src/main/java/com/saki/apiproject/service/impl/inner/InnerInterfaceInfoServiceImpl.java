package com.saki.apiproject.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import com.saki.apiproject.mapper.InterfaceInfoMapper;
import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import com.saki.common.model.entity.InterfaceInfo;
import com.saki.common.service.InnerInterfaceInfoService;
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

    @Override
    public void updateTotal(long interfaceInfoId, long totalInvokes) {
        totalInvokes = totalInvokes + 1;
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(interfaceInfoId);
        interfaceInfo.setTotalInvokes(totalInvokes);
        interfaceInfoMapper.updateById(interfaceInfo);
    }

}
