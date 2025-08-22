// package com.saki.apiproject.exception;
//
//
// import lombok.Getter;
//
// /**
//  * @author JianShang
//  * @version 1.0.0
//  * @description 自定义异常类
//  * @date 2024-09-13 12:04:38
//
//  */
// @Getter
// public class BusinessException extends RuntimeException {
//
//     private final int code;
//
//     public BusinessException(int code, String message) {
//         super(message);
//         this.code = code;
//     }
//
//     public BusinessException(ErrorCode errorCode) {
//         super(errorCode.getMessage());
//         this.code = errorCode.getCode();
//     }
//
//     public BusinessException(ErrorCode errorCode, String message) {
//         super(message);
//         this.code = errorCode.getCode();
//     }
//
// }
