package com.saki.apiproject.model.dto.userinterfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 创建请求
 * @date 2024-09-13 12:11:38
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

}