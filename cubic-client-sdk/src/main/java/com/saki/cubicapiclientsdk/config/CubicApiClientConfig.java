package com.saki.cubicapiclientsdk.config;

import com.saki.cubicapiclientsdk.client.CubicApiClient;
import com.saki.cubicapiclientsdk.service.ApiService;
import com.saki.cubicapiclientsdk.service.impl.ApiServiceImpl;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author sakisaki
 * @version 1.0.0
 * @description JsApi 客户端配置
 * @date 2024-09-13 03:28:17
 */
@Data
@Configuration
@ComponentScan
@ConfigurationProperties("cubic.api.client")
public class CubicApiClientConfig {

    /**
     * 访问密钥
     */
    private String accessKey;
    /**
     * 秘密密钥
     */
    private String secretKey;
    /**
     * 网关
     */
    private String host;

    @Bean
    public CubicApiClient cubicApiClient() {
        return new CubicApiClient(accessKey, secretKey);
    }

    @Bean
    public ApiService apiService() {
        ApiServiceImpl apiServiceImpl = new ApiServiceImpl();
        apiServiceImpl.setCubicApiClient(new CubicApiClient(accessKey, secretKey));
        if (StringUtils.isNotBlank(host)) {
            apiServiceImpl.setGatewayHost(host);
        }
        return apiServiceImpl;
    }
}
