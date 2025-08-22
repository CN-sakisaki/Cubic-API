package com.saki.common.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 签到实体类
 * @author sakisaki
 * @TableName monthly_sign_records 签到表
 * @date 2025/1/17 23:22
 */
@TableName(value = "monthly_sign_records")
@Data
public class MonthlySignRecords implements Serializable {
    @TableField
    private static final long serialVersionUID = 8569016360646212666L;

    /**
     * 主键Id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 签到年月（yyyy-MM）
     */
    private String signMonth;

    /**
     * 该月每天的签到情况
     */
    private String signStatus;

    /**
     * 创建时间
     */
    private Date createTime;
}