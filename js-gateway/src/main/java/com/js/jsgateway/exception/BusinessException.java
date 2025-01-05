package com.js.jsgateway.exception;


import com.js.jsapicommon.common.ErrorCode;
import lombok.Getter;

/**
 * 自定义异常类
 * @author sakisaki
 * @date 2025/1/4 23:50
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}
