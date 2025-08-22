package com.saki.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 请求参数字段
 * @author sakisaki
 * @date 2025/1/11 11:47
 */
@Data
public class RequestParamsField implements Serializable {

    private static final long serialVersionUID = -2602530120976654103L;
    private String id;
    private String fieldName;
    private String type;
    private String desc;
    private String required;
}
