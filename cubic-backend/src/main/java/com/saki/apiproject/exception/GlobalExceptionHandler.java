package com.saki.apiproject.exception;

import com.saki.common.common.BaseResponse;
import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import com.saki.common.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 全局异常处理器
 * @date 2024-09-13 12:04:45
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: {}", e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }
}
