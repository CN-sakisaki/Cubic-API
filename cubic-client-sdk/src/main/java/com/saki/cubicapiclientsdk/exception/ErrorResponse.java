package com.saki.cubicapiclientsdk.exception;

import lombok.Data;

/**
 * 统一错误响应类
 * @author sakisaki
 * @date 2024/12/18 11:13
 */
@Data
public class ErrorResponse {
    private String message;
    private int code;
}
