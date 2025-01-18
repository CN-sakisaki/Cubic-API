package com.js.project.service;

import com.js.project.model.entity.MonthlySignRecords;
import com.baomidou.mybatisplus.extension.service.IService;

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
     * @param day 日期
     * @return boolean
     */
    boolean isSigned(Long userId, int day);

    /**
     * 将签到信息同步到数据库
     *
     * @param userId    用户Id
     * @param signMonth 签到月份 (格式：yyyy-MM)
     */
    String generateSignKey(Long userId, LocalDate signMonth);

    /**
     * 计算用户累计签到天数
     * @param userId 用户Id
     * @return int
     */
    int countTotalSignDays(Long userId);
}
