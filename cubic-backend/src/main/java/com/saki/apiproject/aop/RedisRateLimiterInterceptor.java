package com.saki.apiproject.aop;

import com.saki.apiproject.annotation.RedisRateLimiter;
import com.saki.common.common.ErrorCode;
import com.saki.common.common.ResultUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;


/**
 * 限流拦截器（令牌桶算法）
 * @author sakisaki
 * @date 2025/2/16 12:58
 */
@Aspect
@Component
public class RedisRateLimiterInterceptor {

    private final HttpServletRequest request;
    private final RedisTemplate<String, Object> redisTemplate;
    private DefaultRedisScript<Long> tokenBucketScript;

    public RedisRateLimiterInterceptor(HttpServletRequest request, RedisTemplate<String, Object> redisTemplate) {
        this.request = request;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        tokenBucketScript = new DefaultRedisScript<>();
        tokenBucketScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/token_bucket.lua")));
        tokenBucketScript.setResultType(Long.class);
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
        int capacity = redisRateLimiter.capacity();
        int rate = redisRateLimiter.rate();
        int timeout = redisRateLimiter.timeout();
        String baseKey = redisRateLimiter.value();

        // 生成限流键
        String key = "rate_limit:" + clientId + ":" + methodName + ":" + baseKey;
        // 当前时间戳（秒）
        long now = System.currentTimeMillis() / 1000;

        // 执行Lua脚本获取令牌
        Long result = redisTemplate.execute(
                tokenBucketScript,
                Collections.singletonList(key),
                capacity, rate, now, 1, timeout
        );

        // 返回值说明：1表示成功获取令牌，0表示获取令牌失败
        if (result != null && result == 1) {
            // 成功获取令牌，执行目标方法
            return joinPoint.proceed();
        } else {
            // 获取令牌失败，返回限流提示
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "请求过于频繁，请稍后再试～");
        }
    }
}
