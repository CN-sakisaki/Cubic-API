package com.saki.common.model.dto;

import lombok.Data;

/**
 *
 * @author sakisaki
 * @date 2025/2/4 23:10
 */
@Data
public class ResponseParamsField {
    private String id;
    private String fieldName;
    private String type;
    private String desc;
}