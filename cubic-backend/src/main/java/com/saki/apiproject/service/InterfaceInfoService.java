package com.saki.apiproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.saki.common.model.entity.InterfaceInfo;


/**
 * @author JianShang
 * @version 1.0.0
 * @description 接口信息服务
 * @date 2024-09-13 12:09:18
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
