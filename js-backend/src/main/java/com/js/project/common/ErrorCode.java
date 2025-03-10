package com.js.project.common;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 错误码
 * @date 2024-09-13 12:01:57
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    BALANCE_ERROR(40500, "余额不足"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    PROHIBITED(40001, "账号已封禁"),
    TOO_MANY_REQUEST(42900, "请求过于频繁"),
    SERVICE_UNAVAILABLE(50300, "系统繁忙");


    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
