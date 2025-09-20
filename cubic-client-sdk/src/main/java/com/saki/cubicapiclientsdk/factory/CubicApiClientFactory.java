package com.saki.cubicapiclientsdk.factory;

import com.saki.cubicapiclientsdk.client.CubicApiClient;
import com.saki.cubicapiclientsdk.config.CubicApiClientProperties;
import org.springframework.stereotype.Component;

/**
 * API 客户端工厂，通过工厂模式在每次请求时生成新的 client，同时避免直接操作 Bean
 * @author sakisaki
 * @date 2025/9/20 12:34
 */
@Component
public class CubicApiClientFactory {

    private final CubicApiClientProperties properties;

    public CubicApiClientFactory(CubicApiClientProperties properties) {
        this.properties = properties;
    }

    public CubicApiClient newCubicClient(String accessKey, String secretKey) {
        return new CubicApiClient(accessKey, secretKey, properties.getGatewayHost());
    }
}
