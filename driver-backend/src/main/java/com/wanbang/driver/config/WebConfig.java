package com.wanbang.driver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final DriverInterceptor driverInterceptor;

    public WebConfig(DriverInterceptor driverInterceptor) {
        this.driverInterceptor = driverInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器并指定拦截路径
        registry.addInterceptor(driverInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有/api/开头的请求
                .excludePathPatterns("/api/driver/auth/login","/api/driver/auth/logout");  // 排除登录接口
//                .excludePathPatterns("/**");
    }

}
