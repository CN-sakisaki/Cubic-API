package com.saki.apiproject.constant;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 用户常量
 * @date 2024-09-13 12:03:25
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 系统用户 id（虚拟用户）
     */
    long SYSTEM_USER_ID = 0;

    //  region 权限

    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "admin";

    // endregion
}
