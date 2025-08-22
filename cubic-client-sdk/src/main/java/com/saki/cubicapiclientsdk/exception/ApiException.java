package com.saki.cubicapiclientsdk.exception;

import lombok.Getter;

/**
 * 自定义异常类
 * @author sakisaki
 * @date 2024/12/18 11:08
 */
@Getter
public class ApiException extends Exception {

    private static final long serialVersionUID = -8235451427134795074L;
    private int code;

    /**
     * 调用super(message)来将message传递给父类Exception的构造函数，设置异常的消息
     * @param code 状态码
     * @param message 信息
     */
    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 将message和cause传递给父类Exception的构造函数，用于设置异常的消息和原因
     * @param message 信息
     * @param cause 原因
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 调用super(errorCode.getMessage())将ErrorCode中的message传递给父类Exception的构造函数
     * @param errorCode 状态枚举
     */
    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 调用super(message);来设置异常消息
     * @param errorCode 状态码
     * @param message 信息
     */
    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}
