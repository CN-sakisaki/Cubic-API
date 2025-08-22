package com.saki.apiproject.model.enums;

import lombok.Getter;

/**
 * 错误码
 * @author sakisaki
 * @date 2025/2/7 16:44
 */
@Getter
public enum ImageStatusEnum {
    /**
     * 成功
     */
    SUCCESS("success", "done"),
    /**
     * 参数错误
     */
    ERROR("error", "error");


    /**
     * 状态
     */
    private final String status;

    /**
     * 信息
     */
    private final String value;

    ImageStatusEnum(String status, String value) {
        this.status = status;
        this.value = value;
    }

}
