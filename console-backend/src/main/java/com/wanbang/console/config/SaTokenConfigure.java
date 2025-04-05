package com.wanbang.console.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        "/api/auth/**",          // 排除认证相关的接口
                        "/swagger-ui/**",        // 排除 Swagger UI 所有路径
                        "/swagger-ui.html",      // 显式排除 Swagger UI 入口
                        "/v3/api-docs",          // 排除 OpenAPI 文档
                        "/v3/api-docs/**"        // 确保排除所有可能的 OpenAPI 文档路径
                );
    }


}