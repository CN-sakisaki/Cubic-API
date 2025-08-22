package com.saki.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saki.common.common.BaseResponse;
import com.saki.common.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * 错误web异常处理程序
 * @author sakisaki
 * @date 2025/1/5 10:39
 */
@Configuration
@Slf4j
@Order(-1)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        // 创建数据缓冲区的工厂对象，存放返回给客户端的数据
        DataBufferFactory bufferFactory = response.bufferFactory();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        BaseResponse<String> baseResponse = ResultUtils.error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        log.error("【网关异常】：{}", baseResponse);
        try {
            // 将构建好的异常响应信息序列化为 JSON 格式的字节数据
            byte[] errorBytes = objectMapper.writeValueAsBytes(baseResponse);
            // 使用前面获取到的数据缓冲区工厂创建了一个包含序列化后字节数据的数据缓冲区
            DataBuffer dataBuffer = bufferFactory.wrap(errorBytes);
            // 将这个数据缓冲区包装成一个 Mono，
            return response.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化异常：{}", e.getMessage());
            return Mono.error(e);
        }
    }
}
