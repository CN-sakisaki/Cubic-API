package com.saki.common.common;


import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回值类型
 * @author sakisaki
 * @date 2025/1/5 09:10
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 203245445756919749L;

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
