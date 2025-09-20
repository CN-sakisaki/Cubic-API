package com.saki.cubicapiclientsdk.config;

import com.saki.cubicapiclientsdk.factory.CubicApiClientFactory;
import com.saki.cubicapiclientsdk.service.ApiService;
import com.saki.cubicapiclientsdk.service.impl.ApiServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sakisaki
 * @version 1.0.0
 * @description 向Spring容器注入 CubicApiClientFactory 和 ApiService 这两个Bean，其它可以通过自动注入直接使用
 * @date 2024-09-13 03:28:17
 */
@Configuration
@EnableConfigurationProperties(CubicApiClientProperties.class)
public class CubicApiClientConfig {

    @Bean
    public CubicApiClientFactory cubicApiClientFactory(CubicApiClientProperties properties) {
        return new CubicApiClientFactory(properties);
    }

    @Bean
    public ApiService apiService() {
        return new ApiServiceImpl();
    }
}
