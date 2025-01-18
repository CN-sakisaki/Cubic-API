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

    boolean sign(Long userId);

    boolean isSigned(Long userId, int day);

    String generateSignKey(LocalDate date, Long userId);
}
