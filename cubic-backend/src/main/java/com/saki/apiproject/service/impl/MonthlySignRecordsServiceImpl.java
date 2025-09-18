package com.saki.apiproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.saki.apiproject.constant.SignConstant;
import com.saki.apiproject.mapper.MonthlySignRecordsMapper;
import com.saki.apiproject.service.MonthlySignRecordsService;
import com.saki.apiproject.service.UserService;
import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import com.saki.common.model.entity.MonthlySignRecords;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author sakisaki
 * @description 针对表【monthly_sign_records(签到表)】的数据库操作Service实现
 * @createDate 2025-01-17 21:06:33
 */
@Service
@Slf4j
public class MonthlySignRecordsServiceImpl extends ServiceImpl<MonthlySignRecordsMapper, MonthlySignRecords>
        implements MonthlySignRecordsService {

    @Resource
    private MonthlySignRecordsMapper monthlySignRecordsMapper;

    private final StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    public MonthlySignRecordsServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 签到奖励金币数
     */
    private static final int SIGN_REWARD_COIN = 10;

    /**
     *  签到 Redis Key 过期时间（天）
     */
    private static final int SIGN_KEY_EXPIRE_DAYS = 90;

    /**
     *  连续签到 Key 过期时间（天）
     *  */
    private static final int CONSECUTIVE_KEY_EXPIRE_DAYS = 2;

    /**
     *  Redisson 锁等待时间（秒）
     *  */
    private static final int LOCK_WAIT_TIME = 5;

    /**
     * Redisson 锁自动释放时间（秒）
     * */
    private static final int LOCK_LEASE_TIME = 10;

    /**
     *  日期格式
     *  */
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern(SignConstant.DATE_FORMAT);

    /**
     *  分布式锁前缀
     *  */
    private static final String LOCK_PREFIX = "sign:lock:";


    /**
     * 用户签到
     *
     * @param userId 用户Id
     * @return boolean
     */
    @Override
    public boolean sign(Long userId) {
        LocalDate today = LocalDate.now();
        String signKey = generateSignKey(userId, today);
        int dayOfMonth = today.getDayOfMonth();

        if (isSigned(userId, today)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "今日已签到");
        }

        RLock lock = redissonClient.getLock(LOCK_PREFIX + userId);
        try {
            if (!lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "签到失败，请稍后重试");
            }

            // 执行签到：写入 Redis bitmap
            stringRedisTemplate.opsForValue().setBit(signKey, dayOfMonth - 1, true);
            stringRedisTemplate.expire(signKey, SIGN_KEY_EXPIRE_DAYS, TimeUnit.DAYS);

            // 更新连续签到天数
            String consecutiveKey = generateConsecutiveSignKey(userId);
            int consecutiveDays = calculateConsecutiveSignDays(userId, today, consecutiveKey);
            stringRedisTemplate.opsForValue().set(consecutiveKey, String.valueOf(consecutiveDays), Duration.ofDays(CONSECUTIVE_KEY_EXPIRE_DAYS));

            // 增加金币奖励
            userService.updateUserBalance(userId, SIGN_REWARD_COIN);

            // 异步同步数据库
            CompletableFuture.runAsync(() -> syncSignRecordToDB(userId, getCurrentMonthYear()))
                    .exceptionally(e -> {
                        log.error("同步签到记录失败: userId={}, month={}, error={}", userId, getCurrentMonthYear(), e.getMessage(), e);
                        return null;
                    });

            log.info("用户 {} 签到成功，连续签到天数：{}", userId, consecutiveDays);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "签到失败，请重试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 检查用户是否已签到（支持跨月）
     *
     * @param userId 用户Id
     * @param date   指定日期
     * @return boolean
     */
    @Override
    public boolean isSigned(Long userId, LocalDate date) {
        String key = generateSignKey(userId, date);
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(key, date.getDayOfMonth() - 1));
    }

    /**
     * 计算连续签到天数
     */
    private int calculateConsecutiveSignDays(Long userId, LocalDate today, String consecutiveSignKey) {
        Integer consecutiveSignDays = getConsecutiveSignDaysFromRedis(consecutiveSignKey);
        if (consecutiveSignDays == null || consecutiveSignDays == 0) {
            return 1;
        }

        LocalDate yesterday = today.minusDays(1);
        boolean wasSignedYesterday = isSigned(userId, yesterday);

        return wasSignedYesterday ? consecutiveSignDays + 1 : 1;
    }

    /**
     * 同步签到记录到数据库
     *
     * @param userId              用户Id
     * @param signMonth           签到月份 (格式：yyyy-MM)
     */
    private void syncSignRecordToDB(Long userId, String signMonth) {
        String redisKey = generateSignKey(userId, LocalDate.now());
        int today = LocalDate.now().getDayOfMonth();

        QueryWrapper<MonthlySignRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId).eq("signMonth", signMonth);
        MonthlySignRecords record = monthlySignRecordsMapper.selectOne(queryWrapper);

        if (record != null) {
            updateExistingRecord(record, redisKey, today);
        } else {
            insertNewRecord(userId, signMonth, redisKey, today);
        }
    }

    /**
     * 更新现有签到记录
     *
     * @param record              现有记录
     * @param redisKey            Redis 键
     * @param today               当前日期
     */
    private void updateExistingRecord(MonthlySignRecords record, String redisKey, int today) {
        boolean isSignedToday = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(redisKey, today - 1));
        long status = record.getSignStatus() == null ? 0L : record.getSignStatus();

        if (isSignedToday) {
            status |= (1L << (today - 1));
        }

        record.setSignStatus(status);
        monthlySignRecordsMapper.updateById(record);
    }

    /**
     * 插入新的签到记录
     *
     * @param userId              用户Id
     * @param signMonth           签到月份
     * @param redisKey            Redis 键
     * @param today               当前日期
     */
    private void insertNewRecord(Long userId, String signMonth, String redisKey, int today) {
        long status = 0L;
        for (int day = 1; day <= today; day++) {
            boolean isSigned = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(redisKey, day - 1));
            if (isSigned) {
                // 第 day 天设为 1
                status |= (1L << (day - 1));
            }
        }

        MonthlySignRecords record = new MonthlySignRecords();
        record.setUserId(userId);
        record.setSignMonth(signMonth);
        record.setSignStatus(status);
        monthlySignRecordsMapper.insert(record);
    }


    /**
     * 生成 Redis 键
     *
     * @param userId    用户Id
     * @param date 日期
     * @return String
     */
    private String generateSignKey(Long userId, LocalDate date) {
        return String.format("%s:%s:%d", SignConstant.SIGN_KEY_PREFIX, date.format(MONTH_FORMATTER), userId);
    }

    /**
     * 获取当前年月
     *
     * @return String
     */
    private String getCurrentMonthYear() {
        return LocalDate.now().format(MONTH_FORMATTER);
    }

    /**
     * 计算用户累计签到天数
     *
     * @param userId 用户Id
     * @return int
     */
    @Override
    public int countTotalSignDays(Long userId) {
        return monthlySignRecordsMapper.countTotalSignDays(userId);
    }

    /**
     * 从 Redis 中获取连续签到天数
     *
     * @param consecutiveSignKey Redis 键
     * @return Integer
     */
    @Override
    public Integer getConsecutiveSignDaysFromRedis(String consecutiveSignKey) {
        String days = stringRedisTemplate.opsForValue().get(consecutiveSignKey);
        return days != null ? Integer.parseInt(days) : null;
    }

    /**
     * 生成连续签到 Redis 键
     *
     * @param userId 用户Id
     * @return String
     */
    private String generateConsecutiveSignKey(Long userId) {
        return String.format("%s:%d:%s", SignConstant.CONSECUTIVE_SIGN_PREFIX, userId, SignConstant.CONSECUTIVE_SIGN_DAYS);
    }
}