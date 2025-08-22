package com.saki.apiproject.constant;

/**
 * 签到相关常量
 * @author sakisaki
 * @date 2025/1/17 21:24
 */
public interface SignConstant {
    /**
     * 用户签到 键 前缀
     */
    String SIGN_KEY_PREFIX = "sign:";
    /**
     * 键 的签到时间格式
     */
    String DATE_FORMAT = "yyyy-MM";

    String CONSECUTIVE_SIGN_DAYS = ":consecutiveSignDays";

    String CONSECUTIVE_SIGN_PREFIX = "user:";

}
