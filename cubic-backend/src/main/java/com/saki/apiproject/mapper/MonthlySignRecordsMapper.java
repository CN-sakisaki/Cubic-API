package com.saki.apiproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saki.common.model.entity.MonthlySignRecords;


/**
 * @author jianshang
 * @description 针对表【monthly_sign_records(签到表)】的数据库操作Mapper
 * @createDate 2025-01-17 21:06:33
 * @Entity com.js.project.model.entity.MonthlySignRecords
 */
public interface MonthlySignRecordsMapper extends BaseMapper<MonthlySignRecords> {

    int countTotalSignDays(Long userId);
}




