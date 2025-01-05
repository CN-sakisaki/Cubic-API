package com.js.jsgateway.gateway;

import com.js.jsapicommon.common.ErrorCode;
import com.js.jsapicommon.model.entity.InterfaceInfo;
import com.js.jsapicommon.model.entity.User;
import com.js.jsapicommon.model.enums.InterfaceInfoStatusEnum;
import com.js.jsapicommon.service.InnerInterfaceInfoService;
import com.js.jsapicommon.service.InnerUserInterfaceInfoService;
import com.js.jsapicommon.service.InnerUserService;
import com.js.jsgateway.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.js.jsapiclientsdk.utils.SignUtils.getSign;
import static com.js.jsgateway.utils.NetUtils.getIp;

/**
 * 网关全局过滤器
 * @author sakisaki
 * @date 2025/1/4 22:59
 */
@Slf4j
@Component
public class GatewayGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    /**
     * 请求白名单
     */
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1", "111.230.49.155");
    /**
     * 五分钟过期时间
     */
    private static final long FIVE_MINUTES = 5L * 60;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 输出日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一id：{}", request.getId());
        log.info("请求方法：{}", request.getMethod());
        log.info("请求路径：{}", request.getPath());
        log.info("网关本地地址：{}", request.getLocalAddress());
        log.info("请求远程地址：{}", request.getRemoteAddress());
        log.info("接口请求IP：{}", getIp(request));
        log.info("url:{}", request.getURI());
        return verifyParameters(exchange, chain);
    }

    /**
     * 验证参数
     *
     * @param exchange 交换
     * @param chain    链条
     * @return {@link Mono}<{@link Void}>
     */
    private Mono<Void> verifyParameters(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 请求白名单
        // if (!WHITE_HOST_LIST.contains(getIp(request))) {
        //     throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        // }
        HttpHeaders headers = request.getHeaders();
        String body = headers.getFirst("body");
        String accessKey = headers.getFirst("accessKey");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        // 请求头中参数必须完整
        if (StringUtils.isAnyBlank(body, sign, accessKey, timestamp)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }
        // 防重发XHR
        long currentTime = System.currentTimeMillis() / 1000;
        assert timestamp != null;
        if (currentTime - Long.parseLong(timestamp) >= FIVE_MINUTES) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "会话已过期,请重试！");
        }
        try {
            User user = innerUserService.getInvokeUser(accessKey);
            // 校验用户
            if (user == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "请正确配置接口凭证");
            }
            // 校验accessKey
            if (!user.getAccessKey().equals(accessKey)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "请先获取访问密钥");
            }
            // 校验签名
            if (!getSign(body, user.getSecretKey()).equals(sign)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非法请求");
            }
            String method = Objects.requireNonNull(request.getMethod()).toString();
            String uri = request.getURI().toString().trim();
            // 校验请求路径和请求方法
            if (StringUtils.isAnyBlank(uri, method)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            // 校验接口
            InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(uri, method);
            if (interfaceInfo == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
            }
            // 校验接口状态
            if (interfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口未开启");
            }
            // 下一步处理
            return handleResponse(exchange, chain, user, interfaceInfo);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        }
    }

    /**
     *
     * @param user
     * @param interfaceInfo
     * @return Mono<Void>
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, User user, InterfaceInfo interfaceInfo) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    // writeWith 方法向客户端发送响应内容，
                    // 接收一个 Publisher 类型的参数，这个参数代表了要发送的数据来源，通常是 Flux 或 Mono 类型，里面包含了响应数据对应的 DataBuffer 元素
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        // 记录要发送的响应数据body是否为 Flux 类型
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            // 将传入的 Publisher 类型的 body 转换为 Flux 类型的 fluxBody
                            // Flux 类型，元素是 DataBuffer
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 调用了父类，即 ServerHttpResponseDecorator 的父类 ServerHttpResponse的 writeWith 方法
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        try {
                                            // 调用成功，接口调用次数 + 1
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfo.getId(), user.getId());
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        // 创建DataBuffer 的可读字节数长度的一个字节数组
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        // 将 DataBuffer 中的数据读取到这个字节数组中
                                        dataBuffer.read(content);
                                        //释放掉内存
                                        DataBufferUtils.release(dataBuffer);
                                        // 构建日志
                                        StringBuilder stringBuilder = new StringBuilder(200);
                                        HttpStatus statusCode = originalResponse.getStatusCode();
                                        String data = new String(content, StandardCharsets.UTF_8);
                                        stringBuilder.append("响应状态码：").append(statusCode.value()).append(", ");
                                        stringBuilder.append("响应数据：").append(data);
                                        // 打印日志
                                        log.info(stringBuilder.toString());
                                        // 将读取出来的字节数组 content 重新包装成一个经过业务逻辑和数据处理后新的 DataBuffer
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", originalResponse.getStatusCode());
                            // 调用失败后的降级处理
                            return super.writeWith(body);
                        }
                    }
                };
                // mutate() 方法返回一个 ServerWebExchange.Builder对象
                // 将装饰后的响应对象替换掉原本 ServerWebExchange 实例中的原始响应对象
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 降级处理返回数据
            return chain.filter(exchange);
        }catch (Exception e) {
            log.error("网关处理响应异常 {} ", String.valueOf(e));
            return chain.filter(exchange);
        }
    }

    /**
     * 指定执行顺序，比CacheBodyGatewayFilter 慢执行
     * @return int
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
