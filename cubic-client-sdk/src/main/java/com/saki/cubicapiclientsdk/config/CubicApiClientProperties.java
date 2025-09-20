package com.saki.cubicapiclientsdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 通过外部化配置机制 + 自动装配 读取配置文件里的网关地址
 * @author sakisaki
 * @date 2025/9/20 10:37
 */
@Data
@ConfigurationProperties(prefix = "cubic")
public class CubicApiClientProperties {
    private String gatewayHost;
}