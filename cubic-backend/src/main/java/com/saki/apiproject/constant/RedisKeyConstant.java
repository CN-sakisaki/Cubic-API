package com.saki.apiproject.constant;

/**
 * Redis Key 常量
 * @author sakisaki
 * @date 2025/9/17 22:02
 */
public interface RedisKeyConstant {

    /**
     * 热点接口列表
     */
    String INTERFACE_HOT_LIST = "interface:hot:list";

    /**
     * 单个接口详情 key 前缀，使用时拼接 id
     */
    String INTERFACE_INFO_PREFIX = "interface:info:";
}
