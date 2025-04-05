package com.wanbang.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ManagerInterceptor managerInterceptor;

    public WebConfig(ManagerInterceptor managerInterceptor) {
        this.managerInterceptor = managerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器并指定拦截路径
        registry.addInterceptor(managerInterceptor)
//                .addPathPatterns("/api/**")  // 拦截所有/api/开头的请求
//                .excludePathPatterns("/api/login");  // 排除登录接口
                .excludePathPatterns("/**");  // 排除所有接口
    }

}
