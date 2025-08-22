package com.saki.apiproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.saki.apiproject.constant.SignConstant;
import com.saki.apiproject.mapper.MonthlySignRecordsMapper;
import com.saki.apiproject.service.MonthlySignRecordsService;
import com.saki.apiproject.service.UserService;
import com.saki.apiproject.utils.RedissonLockUtils;
import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import com.saki.common.model.entity.MonthlySignRecords;
import com.saki.common.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    private RedissonLockUtils redissonLockUtils;

    @Resource
    private UserService userService;

    public MonthlySignRecordsServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 用户签到
     *
     * @param userId 用户Id
     * @return boolean
     */
    @Override
    public boolean sign(Long userId) {
        String key = generateSignKey(userId, LocalDate.now());
        int dayOfMonth = LocalDate.now().getDayOfMonth();

        if (isSigned(userId, dayOfMonth)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "今日已签到");
        }

        // 使用分布式锁保证原子性
        redissonLockUtils.redissonDistributedLocks(("sign" + userId).intern(), () -> {
            // 执行签到操作
            stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);

            // 更新连续签到天数
            String consecutiveSignKey = generateConsecutiveSignKey(userId);
            int consecutiveSignDays = calculateConsecutiveSignDays(userId, dayOfMonth, consecutiveSignKey);
            stringRedisTemplate.opsForValue().set(consecutiveSignKey, String.valueOf(consecutiveSignDays), Duration.ofDays(2));
            extracted(userId);
            // 异步同步签到记录到数据库
            CompletableFuture.runAsync(() -> syncSignRecordToDB(userId, getCurrentMonthYear()))
                    .exceptionally(e -> {
                        log.error("同步签到记录到数据库失败: 用户ID={}, 日期={}, {}", userId, getCurrentMonthYear(), e.getMessage());
                        return null;
                    });
        }, "签到失败,请稍后重试");
        log.info("用户 {} 签到成功，连续签到天数：{}", userId, getConsecutiveSignDaysFromRedis(generateConsecutiveSignKey(userId)));
        return true;
    }

    /**
     * 签到成功添加10金币
     * @param userId 用户Id
     */
    private void extracted(Long userId) {
        User user = userService.getById(userId);
        Long balance = user.getBalance();
        user.setBalance(balance + 10);
        userService.updateById(user);
    }

    /**
     * 检查用户是否已签到
     *
     * @param userId 用户Id
     * @param day    日期
     * @return boolean
     */
    @Override
    public boolean isSigned(Long userId, int day) {
        String key = generateSignKey(userId, LocalDate.now().withDayOfMonth(day));
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(key, day - 1));
    }

    /**
     * 计算连续签到天数
     *
     * @param userId            用户Id
     * @param dayOfMonth        当前日期
     * @param consecutiveSignKey Redis 键
     * @return int
     */
    private int calculateConsecutiveSignDays(Long userId, int dayOfMonth, String consecutiveSignKey) {
        Integer consecutiveSignDays = getConsecutiveSignDaysFromRedis(consecutiveSignKey);
        if (consecutiveSignDays == null || consecutiveSignDays == 0) {
            return 1;
        }

        // 判断昨天是否签到
        boolean wasSignedYesterday = false;
        if (dayOfMonth > 1) {
            // 如果是当月第 2 天及以后，直接检查昨天是否签到
            wasSignedYesterday = isSigned(userId, dayOfMonth - 1);
        } else {
            // 如果是当月第 1 天，需要检查上个月的最后一天是否签到
            LocalDate today = LocalDate.now();
            LocalDate lastDayOfLastMonth = today.minusMonths(1).withDayOfMonth(today
                    .minusMonths(1)
                    .lengthOfMonth());
            wasSignedYesterday = isSigned(userId, lastDayOfLastMonth.getDayOfMonth());
        }

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
        StringBuilder signStatusBuilder = new StringBuilder(record.getSignStatus());
        boolean isSignedToday = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(redisKey, today - 1));

        // 补全未签到的天数
        if (today > record.getSignStatus().length()) {
            for (int day = record.getSignStatus().length(); day < today - 1; day++) {
                signStatusBuilder.append("0");
            }
        }

        // 更新今天的签到状态
        if (signStatusBuilder.length() == today - 1 && isSignedToday) {
            signStatusBuilder.append("1");
        }

        record.setSignStatus(signStatusBuilder.toString());
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
        StringBuilder signStatusBuilder = new StringBuilder();
        for (int day = 1; day <= today; day++) {
            boolean isSigned = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(redisKey, day - 1));
            signStatusBuilder.append(isSigned ? "1" : "0");
        }

        MonthlySignRecords record = new MonthlySignRecords();
        record.setUserId(userId);
        record.setSignMonth(signMonth);
        record.setSignStatus(signStatusBuilder.toString());
        monthlySignRecordsMapper.insert(record);
    }

    /**
     * 生成 Redis 键
     *
     * @param userId    用户Id
     * @param signMonth 日期
     * @return String
     */
    private String generateSignKey(Long userId, LocalDate signMonth) {
        return SignConstant.SIGN_KEY_PREFIX + signMonth.format(DateTimeFormatter.ofPattern(SignConstant.DATE_FORMAT)) + ":" + userId;
    }

    /**
     * 获取当前年月
     *
     * @return String
     */
    private String getCurrentMonthYear() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(SignConstant.DATE_FORMAT));
    }

    /**
     * 计算用户累计签到天数
     *
     * @param userId 用户Id
     * @return int
     */
    @Override
    public int countTotalSignDays(Long userId) {
        List<MonthlySignRecords> records = monthlySignRecordsMapper
                .selectList(new QueryWrapper<MonthlySignRecords>().eq("userId", userId));
        return records.stream()
                .mapToInt(record ->
                        (int) record.getSignStatus()
                                .chars()
                                .filter(c -> c == '1')
                                .count())
                .sum();
    }

    /**
     * 从 Redis 中获取连续签到天数
     *
     * @param consecutiveSignKey Redis 键
     * @return Integer
     */
    @Override
    public Integer getConsecutiveSignDaysFromRedis(String consecutiveSignKey) {
        String consecutiveSignDaysStr = stringRedisTemplate.opsForValue().get(consecutiveSignKey);
        return consecutiveSignDaysStr != null ? Integer.parseInt(consecutiveSignDaysStr) : null;
    }

    /**
     * 生成连续签到 Redis 键
     *
     * @param userId 用户Id
     * @return String
     */
    private String generateConsecutiveSignKey(Long userId) {
        return SignConstant.CONSECUTIVE_SIGN_PREFIX + userId + SignConstant.CONSECUTIVE_SIGN_DAYS;
    }
}