package com.saki.cubicapiclientsdk.model.enums;

import lombok.Getter;

/**
 * 请求类型枚举类
 * @author sakisaki
 * @date 2024/12/18 11:03
 */
@Getter
public enum RequestMethodEnum {
    GET("GET", "GET"),
    POST("POST", "POST");
    /**
     * 请求类型描述
     */
    private final String text;
    /**
     * 请求类型
     */
    private final String value;

    RequestMethodEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

}
