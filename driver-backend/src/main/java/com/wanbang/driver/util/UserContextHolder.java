package com.wanbang.driver.util;

public class UserContextHolder {
    private static final ThreadLocal<String> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> userTokenHolder = new ThreadLocal<>();

    public static void setUserId(String userId) {
        System.out.println("设置 userId: " + userId + ", 线程: " + Thread.currentThread().getName());
        userIdHolder.set(userId);
    }

    public static String getUserId() {
        String userId = userIdHolder.get();
        System.out.println("获取 userId: " + userId + ", 线程: " + Thread.currentThread().getName());
        return userId;
    }

    public static void setUserToken(String token) {
        userTokenHolder.set(token);
    }

    public static String getUserToken() {
        return userTokenHolder.get();
    }

    public static void clear() {
        userIdHolder.remove();
        userTokenHolder.remove();
    }
}