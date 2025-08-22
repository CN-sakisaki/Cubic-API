package com.saki.common.service;



/**
 * @author JianShang
 * @version 1.0.0
 * @description 内部用户接口信息服务
 * @date 2024-09-23 09:35:08
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
