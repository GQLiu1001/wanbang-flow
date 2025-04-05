package com.wanbang.manager.util;


public class UserContextHolder {

    // 创建ThreadLocal来存储用户信息
    private static final ThreadLocal<String> USER_CONTEXT = new ThreadLocal<>();

    // 设置用户信息
    public static void setUserId(String userId) {
        USER_CONTEXT.set(userId);
    }

    // 获取用户信息
    public static String getUserId() {
        return USER_CONTEXT.get();
    }

    // 清理ThreadLocal
    public static void clear() {
        USER_CONTEXT.remove();
    }
}
