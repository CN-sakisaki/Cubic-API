package com.saki.apiproject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解用于标记需要限流的方法或接口
 * @author sakisaki
 * @date 2025/2/16 12:56
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisRateLimiter {
    /**
     * 限流键名
     */
    String value();

    /**
     * 令牌桶容量
     */
    int capacity();

    /**
     * 令牌生成速率（每秒生成的令牌数）
     */
    int rate();

    /**
     * 获取令牌的超时时间（秒），默认0表示不等待直接返回
     */
    int timeout() default 0;
}
