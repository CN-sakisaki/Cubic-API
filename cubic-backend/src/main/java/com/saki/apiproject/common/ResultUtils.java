// package com.saki.apiproject.common;
//
// /**
//  * @author JianShang
//  * @version 1.0.0
//  * @description 返回工具类
//  * @date 2024-09-13 12:02:28
//  */
// public class ResultUtils {
//
//     /**
//      * 成功
//      *
//      * @param data
//      * @param <T>
//      * @return
//      */
//     public static <T> BaseResponse<T> success(T data) {
//         return new BaseResponse<>(0, data, "ok");
//     }
//
//     /**
//      * 失败
//      *
//      * @param errorCode
//      * @return
//      */
//     public static BaseResponse<?> error(ErrorCode errorCode) {
//         return new BaseResponse<>(errorCode);
//     }
//
//     /**
//      * 失败
//      *
//      * @param code
//      * @param message
//      * @return
//      */
//     public static BaseResponse<?> error(int code, String message) {
//         return new BaseResponse<>(code, null, message);
//     }
//
//     /**
//      * 错误
//      *
//      * @param data      数据
//      * @param errorCode 错误代码
//      * @param message   消息
//      * @return {@link BaseResponse}<{@link T}>
//      */
//     public static <T> BaseResponse<T> error(T data, ErrorCode errorCode, String message) {
//         return new BaseResponse<>(errorCode.getCode(), data, message);
//     }
//
//     /**
//      * 失败
//      *
//      * @param errorCode
//      * @return
//      */
//     public static BaseResponse<?> error(ErrorCode errorCode, String message) {
//         return new BaseResponse<>(errorCode.getCode(), null, message);
//     }
// }
