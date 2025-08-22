package com.saki.apigateway.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 * @author sakisaki
 * @date 2025/1/13 20:54
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    /**
     * 地址
     */
    private String host;

    /**
     * 端口号
     */
    private String port;

    /**
     * 密码
     */
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        // 设置 Redis 服务器的地址和要使用的数据库编号
        config.useSingleServer()
                .setAddress(redisAddress)
                .setDatabase(4)
                .setPassword(password);
        // 2. 创建实例
        return Redisson.create(config);
    }
}
