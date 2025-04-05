package com.wanbang.manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration // 或者 @Component
@ConfigurationProperties(prefix = "spring.datasource.secondary")
public class SecondaryDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}