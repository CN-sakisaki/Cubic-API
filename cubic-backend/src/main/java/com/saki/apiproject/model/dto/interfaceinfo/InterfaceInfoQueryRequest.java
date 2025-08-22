package com.saki.apiproject.model.dto.interfaceinfo;


import com.saki.apiproject.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 查询请求
 * @date 2024-09-13 12:06:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 返回格式(JSON等等)
     */
    private String returnFormat;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态 （0-关闭， 1-开启）
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 扣除积分数
     */
    private Long reduceScore;

    /**
     * 创建时间
     */
    private Date createTime;
}