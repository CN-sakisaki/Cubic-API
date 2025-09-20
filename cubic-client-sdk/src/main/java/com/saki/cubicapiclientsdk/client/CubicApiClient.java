package com.saki.cubicapiclientsdk.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 调用第三方接口的客户端
 * @date 2024-09-13 03:27:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CubicApiClient {

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 网关
     */
    private String gatewayHost;
}
