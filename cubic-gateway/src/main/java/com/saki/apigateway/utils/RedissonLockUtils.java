package com.saki.apigateway.utils;

import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson锁工具
 * @author sakisaki
 * @date 2025/1/13 21:16
 */
@Slf4j
@Component
public class RedissonLockUtils {

    @Resource
    public RedissonClient redissonClient;

    /**
     * redisson分布式锁
     * @param lockName 锁名称
     * @param supplier 一个Supplier<T>函数式接口，代表一个无参但能返回 T 类型结果的方法，用于执行加锁后需要进行的业务逻辑操作
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @return T 类型结果
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, ErrorCode errorCode, String errorMessage) {
        // 获取一个 RLock 实例，根据锁名称 lockName 创建或获取对应的分布式锁对象
        RLock rLock = redissonClient.getLock(lockName);
        try {
            // tryLock 方法尝试加锁，因为它是非阻塞的，会立即返回加锁结果
            // 0 表示不等待立即尝试加锁，-1 表示加锁后不自动释放锁
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                // 如果加锁成功，执行 Supplier<T> 接口中的业务逻辑并返回结果
                return supplier.get();
            }
            throw new BusinessException(errorCode.getCode(), errorMessage);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            // 检查当前线程是否持有锁，如果持有则进行解锁操作，以防止其他线程误解锁
            if (rLock.isHeldByCurrentThread()) {
                // 记录解锁操作和解锁时的线程 ID
                log.error("unLock: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁
     *
     * @param waitTime     尝试加锁时的等待时间
     * @param leaseTime    加锁成功后锁的持有时间
     * @param timeUnit     时间单位(TimeUnit.SECONDS 或 TimeUnit.MILLISECONDS)
     * @param lockName     锁名称
     * @param supplier     无参方法，当成功获取锁后将调用该方法执行相应的业务逻辑并返回 T 类型的结果
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @param args         可变参数列表，可用于向 supplier 传递额外的参数
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(long waitTime, long leaseTime, TimeUnit timeUnit, String lockName, Supplier<T> supplier, ErrorCode errorCode, String errorMessage, Object... args) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(waitTime, leaseTime, timeUnit)) {
                return supplier.get();
            }
            throw new BusinessException(errorCode.getCode(), errorMessage);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁
     *
     * @param time         时间
     * @param timeUnit     时间单位
     * @param lockName     锁名称
     * @param supplier     无参方法，当成功获取锁后将调用该方法执行相应的业务逻辑并返回 T 类型的结果
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(long time, TimeUnit timeUnit, String lockName, Supplier<T> supplier, ErrorCode errorCode, String errorMessage) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            // 等待 time 时长，时间单位为 timeUnit。
            // 如果在该时间内成功获取锁，该方法将返回 true，否则返回 false。
            // 阻塞式加锁，当前线程会等待一段时间尝试加锁
            if (rLock.tryLock(time, timeUnit)) {
                return supplier.get();
            }
            throw new BusinessException(errorCode.getCode(), errorMessage);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁
     *
     * @param lockName  锁名称
     * @param supplier  无参方法，当成功获取锁后将调用该方法执行相应的业务逻辑并返回 T 类型的结果
     * @param errorCode 错误代码
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, ErrorCode errorCode) {
        return redissonDistributedLocks(lockName, supplier, errorCode, errorCode.getMessage());
    }

    /**
     * redisson分布式锁
     *
     * @param lockName     锁名称
     * @param supplier     无参方法，当成功获取锁后将调用该方法执行相应的业务逻辑并返回 T 类型的结果
     * @param errorMessage 错误消息
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, String errorMessage) {
        return redissonDistributedLocks(lockName, supplier, ErrorCode.OPERATION_ERROR, errorMessage);
    }

    /**
     * redisson分布式锁
     *
     * @param lockName 锁名称
     * @param supplier 无参方法，当成功获取锁后将调用该方法执行相应的业务逻辑并返回 T 类型的结果
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier) {
        return redissonDistributedLocks(lockName, supplier, ErrorCode.OPERATION_ERROR);
    }

    /**
     * redisson分布式锁
     * 适用于不能等待且需要长时间持有锁的业务场景
     * @param lockName     锁名称
     * @param runnable     Runnable 接口的实例，代表一个无参无返回值的任务，当成功获取锁后将执行该任务
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, ErrorCode errorCode, String errorMessage) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            // tryLock 方法尝试加锁，因为它是非阻塞的，会立即返回加锁结果
            // 0 表示不等待立即尝试加锁，-1 表示加锁后不自动释放锁
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                runnable.run();
            } else {
                throw new BusinessException(errorCode.getCode(), errorMessage);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("lockName:{},unLockId:{} ", lockName, Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁 可自定义 waitTime 、leaseTime、TimeUnit
     *
     * @param waitTime     尝试加锁时的等待时间
     * @param leaseTime    加锁成功后锁的持有时间
     * @param timeUnit         时间单位(TimeUnit. SECONDS 或 TimeUnit. MILLISECONDS)
     * @param lockName     锁名称
     * @param runnable     Runnable 接口的实例，代表一个无参无返回值的任务，当成功获取锁后将执行该任务
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public void redissonDistributedLocks(long waitTime, long leaseTime, TimeUnit timeUnit, String lockName, Runnable runnable, ErrorCode errorCode, String errorMessage) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(waitTime, leaseTime, timeUnit)) {
                runnable.run();
            } else {
                throw new BusinessException(errorCode.getCode(), errorMessage);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁 可自定义 time 、timeUnit
     *
     * @param time         时间,如果锁已被其他线程持有，当前线程将等待的时长
     * @param timeUnit     时间单位
     * @param lockName     锁名称
     * @param runnable     一个 Runnable 接口的实例，用于定义需要在加锁后执行的业务逻辑，该接口的 run() 方法将在加锁成功后被调用
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public void redissonDistributedLocks(long time, TimeUnit timeUnit, String lockName, Runnable runnable, ErrorCode errorCode, String errorMessage) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            // 方法会阻塞当前线程，直到加锁成功或等待时间超时。如果加锁成功，它将返回 true，否则返回 false
            if (rLock.tryLock(time, timeUnit)) {
                runnable.run();
            } else {
                throw new BusinessException(errorCode.getCode(), errorMessage);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁
     *
     * @param lockName  锁名称
     * @param runnable  一个 Runnable 接口的实例，用于定义需要在加锁后执行的业务逻辑，该接口的 run() 方法将在加锁成功后被调用
     * @param errorCode 错误代码
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, ErrorCode errorCode) {
        redissonDistributedLocks(lockName, runnable, errorCode, errorCode.getMessage());
    }

    /**
     * redisson分布式锁
     *
     * @param lockName     锁名称
     * @param runnable     一个 Runnable 接口的实例，用于定义需要在加锁后执行的业务逻辑，该接口的 run() 方法将在加锁成功后被调用
     * @param errorMessage 错误消息
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, String errorMessage) {
        redissonDistributedLocks(lockName, runnable, ErrorCode.OPERATION_ERROR, errorMessage);
    }

    /**
     * redisson分布式锁
     *
     * @param lockName 锁名称
     * @param runnable 一个 Runnable 接口的实例，用于定义需要在加锁后执行的业务逻辑，该接口的 run() 方法将在加锁成功后被调用
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable) {
        redissonDistributedLocks(lockName, runnable, ErrorCode.OPERATION_ERROR, ErrorCode.OPERATION_ERROR.getMessage());
    }

}
