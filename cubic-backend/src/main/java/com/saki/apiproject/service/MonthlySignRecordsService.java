package com.saki.apiproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.saki.common.model.entity.MonthlySignRecords;

import java.time.LocalDate;


/**
 * @author jianshang
 * @description 针对表【monthly_sign_records(签到表)】的数据库操作Service
 * @createDate 2025-01-17 21:06:33
 */
public interface MonthlySignRecordsService extends IService<MonthlySignRecords> {

    /**
     * 签到
     * @param userId 用户Id
     * @return boolean
     */
    boolean sign(Long userId);

    /**
     * 检查本月指定日期用户是否已经签到
     * @param userId 用户Id
     * @param date 日期
     * @return boolean
     */
    boolean isSigned(Long userId, LocalDate date);

    /**
     * 计算用户累计签到天数
     * @param userId 用户Id
     * @return int
     */
    int countTotalSignDays(Long userId);

    /**
     * 从Redis中获取用户连续签到的数据
     * @param consecutiveSignKey 存储的键
     * @return Integer
     */
    Integer getConsecutiveSignDaysFromRedis(String consecutiveSignKey);
}
