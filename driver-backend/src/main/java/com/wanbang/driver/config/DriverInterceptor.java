package com.wanbang.driver.config;

import com.wanbang.driver.util.UserContextHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class DriverInterceptor implements HandlerInterceptor {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("拦截器触发，请求路径: " + request.getRequestURI() + ", 方法: " + request.getMethod() + ", 线程: " + Thread.currentThread().getName());
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length()).trim();
            String userId = stringRedisTemplate.opsForValue().get("delivery:login:" + token);
            System.out.println("token: " + token + ", userId: " + userId);
            if (userId != null && !userId.isEmpty()) {
                UserContextHolder.setUserId(userId);
                UserContextHolder.setUserToken(token);
                System.out.println("设置 UserContextHolder, userId: " + userId);
                return true;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                System.out.println("token 无效，返回 401");
                return false;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        System.out.println("Authorization 缺失或格式错误，返回 401");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println("清理 ThreadLocal, 线程: " + Thread.currentThread().getName());
        UserContextHolder.clear();
    }
}
