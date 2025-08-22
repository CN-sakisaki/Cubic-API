package com.saki.apiproject.common;


import com.saki.apiproject.constant.CommonConstant;
import lombok.Data;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 分页请求
 * @date 2024-09-13 12:02:21
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private long current = 1;

    /**
     * 页面大小
     */
    private long pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
