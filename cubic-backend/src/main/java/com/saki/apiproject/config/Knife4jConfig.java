package com.saki.apiproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author JianShang
 * @version 1.0.0
 * @description Knife4j 接口文档配置
 * https://doc.xiaominfo.com/knife4j/documentation/get_start.html
 * @date 2024-09-13 12:02:52
 */
@Configuration
@EnableSwagger2
@Profile("dev")
public class Knife4jConfig {

    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("project-backend")
                        .description("project-backend")
                        .version("1.0")
                        .build())
                .select()
                // 指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.js.project.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}