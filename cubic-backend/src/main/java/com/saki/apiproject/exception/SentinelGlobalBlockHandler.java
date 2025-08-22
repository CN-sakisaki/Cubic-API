package com.saki.apiproject.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;

import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sentinel公共限流处理类
 * @author sakisaki
 * @date 2025/2/26 16:42
 */
@Component
public class SentinelGlobalBlockHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        ErrorCode errorCode;
        String message;

        // 判断异常类型
        if (e instanceof FlowException) {
            errorCode = ErrorCode.TOO_MANY_REQUEST;
            message = "请求过于频繁，请稍后重试";
        } else if (e instanceof DegradeException) {
            errorCode = ErrorCode.SERVICE_UNAVAILABLE;
            message = "服务暂时不可用，请稍后再试";
        } else {
            errorCode = ErrorCode.SYSTEM_ERROR;
            message = ErrorCode.SYSTEM_ERROR.getMessage();
        }

        // 抛出业务异常，由全局异常处理器统一处理
        throw new BusinessException(errorCode, message);
    }

}
