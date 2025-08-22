package com.saki.apiproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.saki.common.model.entity.UserInterfaceInfo;


/**
 * @author JianShang
 * @version 1.0.0
 * @description 用户接口信息服务
 * @date 2024-09-13 12:09:09
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {


    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
