package com.saki.apigateway.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.saki.apigateway.utils.NetUtils.getIp;


/**
 * 缓存请求体网关筛选器
 * @author sakisaki
 * @date 2025/1/5 13:17
 */
@Component
@Slf4j
public class CacheBodyGatewayFilter implements Ordered, GlobalFilter {

    public static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 输出日志
        ServerHttpRequest request = exchange.getRequest();
        // log.info("请求唯一id：{}", request.getId());
        log.info("请求方法：{}", request.getMethod());
        log.info("请求路径：{}", request.getPath());
        // log.info("网关本地地址：{}", request.getLocalAddress());
        // log.info("请求远程地址：{}", request.getRemoteAddress());
        log.info("接口请求IP：{}", getIp(request));
        log.info("url:{}", request.getURI());

        if (exchange.getRequest().getHeaders().getContentType() == null) {
            // 将请求原封不动地传递给过滤器链中的下一个过滤器继续处理，不做额外的操作，可能当前过滤器并不适用于这种没有内容类型定义的请求情况
            return chain.filter(exchange);
        } else {
            // 获取当前请求的请求体数据流，请求体数据可能是分块传输的
            Flux<DataBuffer> body = exchange.getRequest().getBody();
            // 将分块的请求体数据流合并成一个单一的 Mono<DataBuffer>，方便整体地处理请求体
            Mono<DataBuffer> join = DataBufferUtils.join(body);
            Mono<Void> mono = join.flatMap(new Function<DataBuffer, Mono<Void>>() {
                @Override
                public Mono<Void> apply(DataBuffer dataBuffer) {
                    // 增加数据缓冲区的引用计数，确保这个数据缓冲区不会在后续操作中被误释放
                    DataBufferUtils.retain(dataBuffer);
                    // 创建一个延迟加载的 Flux<DataBuffer>，用于缓存请求体数据
                    // 只有当有订阅者订阅这个 Flux 时，才会执行内部逻辑去获取请求体数据切片后的 Flux
                    Flux<DataBuffer> cachedFlux = Flux.defer(new Supplier<Flux<DataBuffer>>() {
                        @Override
                        public Flux<DataBuffer> get() {
                            // 创建一个只包含单个元素，即对原始数据缓冲区进行切片操作得到的数据缓冲区的 Flux
                            // 获取从起始位置到整个数据缓冲区可读字节长度的部分
                            Flux<DataBuffer> bufferFlux = Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount()));
                            return bufferFlux;
                        }
                    });
                    ServerHttpRequest request = exchange.getRequest();
                    // 创建一个装饰后的请求对象
                    ServerHttpRequestDecorator mutatedRequest = new ServerHttpRequestDecorator(request) {
                        // 获取请求体数据时将返回缓存后的请求体数据
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedFlux;
                        }
                    };
                    // 将缓存的请求体数据对应的 Flux 存储到 ServerWebExchange 对象的属性中
                    exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, cachedFlux);
                    // 通过 exchange.mutate() 获取一个 ServerWebExchange.Builder 对象，
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            // 将前面创建的装饰后的请求对象替换到这个构建器中的请求部分
                            .request(mutatedRequest)
                            .build();
                    // 将修改后的 ServerWebExchange 对象传递给过滤器链中的下一个过滤器进行处理
                    // 使得后续的过滤器以及最终的目标服务接收到的是带有缓存请求体数据的修改后的请求
                    return chain.filter(mutatedExchange);
                }
            });
            return mono;
            // Lambda写法
            // return DataBufferUtils.join(body)
            //         .flatMap(dataBuffer -> {
            //             DataBufferUtils.retain(dataBuffer);
            //             Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
            //             ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
            //                     exchange.getRequest()) {
            //                 @Override
            //                 public Flux<DataBuffer> getBody() {
            //                     return cachedFlux;
            //                 }
            //
            //             };
            //             exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, cachedFlux);
            //
            //             return chain.filter(exchange.mutate().request(mutatedRequest).build());
            //         });
        }
    }

    /**
     * 定义组件的顺序
     * @return int
     */
    @Override
    public int getOrder() {
        // 在整个排序体系中具有最高的优先级
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
