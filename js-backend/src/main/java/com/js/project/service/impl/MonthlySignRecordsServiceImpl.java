package com.js.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.js.project.common.ErrorCode;
import com.js.project.constant.SignConstant;
import com.js.project.exception.BusinessException;
import com.js.project.mapper.MonthlySignRecordsMapper;
import com.js.project.model.entity.MonthlySignRecords;
import com.js.project.service.MonthlySignRecordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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


    public MonthlySignRecordsServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 签到
     * @param userId 用户Id
     * @return boolean
     */
    @Override
    public boolean sign(Long userId) {
        // 生成 Redis 键
        String key = generateSignKey(userId, LocalDate.now());
        // 获取当前日期是当前月的第几天。
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        if (!isSigned(userId, dayOfMonth)) {
            // 用户未签到，执行签到操作
            stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
            // 将签到信息同步到数据库
            // 使用 CompletableFuture 异步执行同步操作
            CompletableFuture.runAsync(() -> {
                try {
                    syncSignRecordToDB(userId, getCurrentMonthYear());
                } catch (Exception e) {
                    // 异步任务异常日志记录
                    log.error("同步签到记录到数据库失败: 用户ID={}, 日期={}, {}", userId, getCurrentMonthYear(), e.getMessage());
                }
            });
            return true;
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "今日已签到");
        }
    }

    /**
     * 检查本月指定日期用户是否已经签到
     * @param userId 用户Id
     * @param day 日期
     * @return boolean
     */
    @Override
    public boolean isSigned(Long userId, int day) {
        String key = generateSignKey(userId, LocalDate.now().withDayOfMonth(day));
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(key, day - 1));
    }

    /**
     * 将签到信息同步到数据库
     * @param userId 用户Id
     * @param signMonth 签到月份 (格式：yyyy-MM)
     */
    private void syncSignRecordToDB(Long userId, String signMonth) {
        String redisKey = generateSignKey(userId, LocalDate.now());
        // 获取今天是本月的第几天
        int today = LocalDate.now().getDayOfMonth();

        // 查询数据库是否已有记录
        QueryWrapper<MonthlySignRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId).eq("signMonth", signMonth);
        MonthlySignRecords record = monthlySignRecordsMapper.selectOne(queryWrapper);
        // 如果当月已有签到记录
        if (record != null) {
            String existingSignStatus = record.getSignStatus();
            // 在原记录上进行拼接操作
            StringBuilder signStatusBuilder = new StringBuilder(existingSignStatus);
            // 天数-1 对应Redis中BitMap的 偏移量
            boolean isSignedToday = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(redisKey, today - 1));
            // 如果中间有 未签到 则补0
            if (today > existingSignStatus.length()) {
                // 如果最后一次记录的天数 小于 当前天数-1，说明除了今天外，期间有未签到
                // 例如记录16号时 day=16，如果今天是18号， 16 < (18 - 1), 说明期间有一天没有签到，就 补0
                for (int day = existingSignStatus.length(); day < today - 1; day++) {
                    signStatusBuilder.append("0");
                }
            }
            // 拼接今天到签到信息
            signStatusBuilder.append(isSignedToday ? "1" : "0");
            record.setSignStatus(signStatusBuilder.toString());
            monthlySignRecordsMapper.update(record, queryWrapper);
        } else {
            StringBuilder signStatusBuilder = new StringBuilder();
            // 如果没有现有记录，直接生成新的签到状态
            for (int day = 1; day <= today; day++) {
                boolean isSigned = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(redisKey, day - 1));
                signStatusBuilder.append(isSigned ? "1" : "0");
            }
            // 插入新记录
            record = new MonthlySignRecords();
            record.setUserId(userId);
            record.setSignMonth(signMonth);
            record.setSignStatus(signStatusBuilder.toString());
            monthlySignRecordsMapper.insert(record);
        }
    }

    /**
     * 构建 Redis 的键，格式为 "sign{当前年月}{userId}"，用于存储用户的签到信息
     *
     * @param userId    用户Id
     * @param signMonth 日期
     * @return String
     */
    @Override
    public String generateSignKey(Long userId, LocalDate signMonth) {
        return SignConstant.SIGN_KEY_PREFIX + signMonth.format(DateTimeFormatter.ofPattern(SignConstant.DATE_FORMAT)) + ":" + userId;
    }

    /**
     * 获取当前的日期
     * @return String
     */
    private String getCurrentMonthYear() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(SignConstant.DATE_FORMAT));
    }


    /**
     * 计算日期的偏移量
     * @param date 日期
     * @return int
     */
    private int getOffset(LocalDate date) {
        // 计算从本月第一天开始到指定日期的天数差，然后转换为偏移量
        // 如果 date 是 2025 年 1 月 17 日，date.toEpochDay() 会返回一个表示从 1970 年 1 月 1 日到 2025 年 1 月 17 日的天数
        // 如果 date 是 2025 年 1 月 17 日，date.withDayOfMonth(1) 将返回一个表示 2025 年 1 月 1 日的 LocalDate 对象
        return (int) (date.toEpochDay() - date.withDayOfMonth(1).toEpochDay());
    }


    /**
     * 计算用户累计签到天数
     * @param userId 用户Id
     * @return int
     */
    @Override
    public int countTotalSignDays(Long userId) {
        // 查询该用户的所有签到记录
        List<MonthlySignRecords> records = monthlySignRecordsMapper
                .selectList(new QueryWrapper<MonthlySignRecords>().eq("userId", userId));
        int totalSignDays = 0;
        // 遍历每个月的签到记录
        for (MonthlySignRecords record : records) {
            String signStatus = record.getSignStatus();
            if (signStatus != null) {
                // 统计该月份签到的天数
                for (char c : signStatus.toCharArray()) {
                    if (c == '1') {
                        totalSignDays++;
                    }
                }
            }
        }
        return totalSignDays;
    }
}




