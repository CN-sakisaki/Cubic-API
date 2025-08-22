package com.saki.apiproject.service.impl.inner;



import com.saki.apiproject.service.UserInterfaceInfoService;
import com.saki.common.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 内部用户接口信息服务实现类
 * @date 2024-09-13 12:10:00
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

}
