package com.wanbang.manager.config;

import com.wanbang.manager.util.UserContextHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ManagerInterceptor implements HandlerInterceptor {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 在请求处理之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取 Authorization
        String authHeader = request.getHeader("Authorization");

        // 检查 Authorization 是否存在且格式正确
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 提取 token
            String token = authHeader.substring("Bearer ".length()).trim();
            String userId =(String) stringRedisTemplate.opsForValue().get("satoken:login:" + token);
            if (userId != null && !userId.isEmpty()) {
                // 将用户 ID 存入 ThreadLocal
                UserContextHolder.setUserId(userId);
                return true; // 验证通过，继续处理请求
            } else {
                System.out.println("用户不存在");
                // token 无效，返回 401 未授权
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }

        // Authorization 头缺失或格式错误，返回 401 未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        System.out.println("头缺失");
        return false;
    }

    // 在请求处理完成后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 清理 ThreadLocal，防止内存泄漏
        UserContextHolder.clear();
    }
}