package com.qwb.petmanage.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类（基于springdoc-openapi）
 */
@Configuration
@EnableKnife4j
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("宠物领养管理系统API")
                        .description("宠物领养管理系统接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("QWB")
                                .email("qwb@example.com")));
    }
}
