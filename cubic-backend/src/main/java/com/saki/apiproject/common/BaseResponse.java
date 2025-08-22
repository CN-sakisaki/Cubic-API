// package com.saki.apiproject.common;
//
// import lombok.Data;
//
// import java.io.Serializable;
//
// /**
//  * @author JianShang
//  * @version 1.0.0
//  * @description 通用返回类
//  * @date 2024-09-13 12:01:37
//  */
// @Data
// public class BaseResponse<T> implements Serializable {
//
//     private static final long serialVersionUID = 4558190455389293451L;
//
//     private int code;
//
//     private T data;
//
//     private String message;
//
//     public BaseResponse(int code, T data, String message) {
//         this.code = code;
//         this.data = data;
//         this.message = message;
//     }
//
//     public BaseResponse(int code, T data) {
//         this(code, data, "");
//     }
//
//     public BaseResponse(ErrorCode errorCode) {
//         this(errorCode.getCode(), null, errorCode.getMessage());
//     }
// }
