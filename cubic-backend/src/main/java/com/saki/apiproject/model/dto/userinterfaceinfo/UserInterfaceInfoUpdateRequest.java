package com.saki.apiproject.model.dto.userinterfaceinfo;


import lombok.Data;

import java.io.Serializable;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 更新请求
 * @date 2024-09-13 12:11:27
 */
@Data
public class UserInterfaceInfoUpdateRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 0-正常，1-禁用
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}