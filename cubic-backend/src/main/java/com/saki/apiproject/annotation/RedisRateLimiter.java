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
     *  限流阈值
     */
    int limit();

    /**
     * 时间窗口大小（秒），默认 10 秒
     */
    int window() default 10;
}
