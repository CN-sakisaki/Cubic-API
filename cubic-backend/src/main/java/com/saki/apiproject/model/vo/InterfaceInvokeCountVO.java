package com.saki.apiproject.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用次数分析 封装视图
 * @author sakisaki
 * @date 2025/9/18 00:39
 */
@Data
public class InterfaceInvokeCountVO implements Serializable {

    private static final long serialVersionUID = -2225595519586535280L;

    /**
     * 接口ID
     */
    private Long id;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口总调用次数
     */
    private Long totalInvokes;
}
