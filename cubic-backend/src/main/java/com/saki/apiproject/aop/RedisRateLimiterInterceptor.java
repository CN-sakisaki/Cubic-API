package com.saki.apiproject.aop;

import com.saki.apiproject.annotation.RedisRateLimiter;

import com.saki.common.common.ErrorCode;
import com.saki.common.common.ResultUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.Duration;


/**
 * 限流拦截器
 * @author sakisaki
 * @date 2025/2/16 12:58
 */
@Aspect
@Component
public class RedisRateLimiterInterceptor {

    private final RedissonClient redissonClient;

    private final HttpServletRequest request;

    public RedisRateLimiterInterceptor(RedissonClient redissonClient, HttpServletRequest request) {
        this.redissonClient = redissonClient;
        this.request = request;
    }

    /**
     * 环绕通知方法，拦截带有 RedisRateLimiter 注解的方法
     * @param joinPoint 切入点对象，用于获取目标方法的相关信息
     * @param redisRateLimiter 从注解中获取的 RedisRateLimiter 实例
     * @return 目标方法的返回值
     * @throws Throwable 可能抛出的异常
     */
    @Around("@annotation(redisRateLimiter)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, RedisRateLimiter redisRateLimiter) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取目标方法
        Method method = signature.getMethod();
        String methodName = method.getName();

        // IP 地址作为客户端标识，限制单个客户端
        String clientId = request.getRemoteAddr();

        // 获取注解中的相关参数
        int limit = redisRateLimiter.limit();
        int window = redisRateLimiter.window();
        String baseKey = redisRateLimiter.value();
        // 获取当前时间戳（毫秒）
        long currentTime = System.currentTimeMillis();
        // 生成动态的限流键，结合当前时间窗口
        long currentWindow = System.currentTimeMillis() / (window * 1000L);
        String key = clientId + ":" + methodName + ":" + baseKey + ":" + currentWindow;
        // 获取有序集合，用于存储请求记录
        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet(key);

        // 移除滑动窗口外的请求记录
        // 当 window = 10 时，移除当前时间往前推 10 秒之前的所有请求记录，保证只统计当前 10 秒滑动窗口内的请求数量。
        sortedSet.removeRangeByScore(0, true, currentTime - window * 1000L, false);

        // 统计滑动窗口内的请求数量
        long count = sortedSet.size();

        if (count >= limit) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "请求过于频繁，请稍后再试～");
        }

        // 将当前请求的时间戳作为分数和元素添加到有序集合中，记录本次请求。
        sortedSet.add(currentTime, currentTime);

        // 设置有序集合的过期时间，避免数据无限增长
        Duration expirationDuration = Duration.ofSeconds(window);
        sortedSet.expire(expirationDuration);

        return joinPoint.proceed();
    }
}
