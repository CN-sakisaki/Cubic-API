package com.saki.apiproject.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author sakisaki
 * @date 2025/2/6 20:55
 */
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class EmailConfig {
    private String emailFrom;
}
