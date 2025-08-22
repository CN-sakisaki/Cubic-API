package com.saki.apiproject.model.dto.interfaceinfo;

import com.saki.common.model.dto.RequestParamsField;
import com.saki.common.model.dto.ResponseParamsField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 接口创建请求
 * @date 2024-09-13 12:06:23
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 接口名称
     */
    private String name;
    /**
     * 返回格式
     */
    private String returnFormat;
    /**
     * 接口地址
     */
    private String url;
    /**
     * 接口响应参数
     */
    private List<ResponseParamsField> responseParams;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 接口请求参数
     */
    private List<RequestParamsField> requestParams;
    /**
     * 描述信息
     */
    private String description;
    /**
     * 请求示例
     */
    private String requestExample;
    /**
     * 请求头
     */
    private String requestHeader;
    /**
     * 响应头
     */
    private String responseHeader;


}