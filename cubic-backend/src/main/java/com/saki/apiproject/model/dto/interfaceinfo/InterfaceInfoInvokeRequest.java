package com.saki.apiproject.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 接口调用请求
 * @date 2024-09-13 12:06:17
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    private static final long serialVersionUID = 8642052222096688593L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户请求参数
     */
    private List<Field> requestParams;

    @Data
    public static class Field {
        /**
         * 字段名
         */
        private String fieldName;
        /**
         * 字段值
         */
        private String value;
    }
}