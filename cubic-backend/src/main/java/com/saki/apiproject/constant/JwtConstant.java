package com.saki.apiproject.constant;

/**
 * JWT常量
 * @author sakisaki
 * @date 2025/2/15 17:28
 */
public interface JwtConstant {

    /**
     * 密钥
     */
    String SECRET_KEY = "64gbe45fgw46765rfdfbd_jwt_secret_key";
    /**
     * 单位 second
     */
    long EXPIRATION_TIME = 60 * 60 * 24;

    /**
     * access_token 前缀
     */
    String ACCESS_TOKEN_PREFIX = "access_token:";

    /**
     * 刷新token 前缀
     */
    String REFRESH_TOKEN_PREFIX = "refresh_token:";
}
