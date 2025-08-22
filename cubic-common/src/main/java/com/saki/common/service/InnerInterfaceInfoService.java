package com.saki.common.service;


import com.saki.common.model.entity.InterfaceInfo;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 内部接口信息服务
 * @date 2024-09-23 09:35:12
 */
public interface InnerInterfaceInfoService {

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

    void updateTotal(long interfaceId, long total);
}
