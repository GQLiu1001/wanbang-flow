package com.wanbang.driver.util;


public class UserContextHolder {
    private static final ThreadLocal<String> USER_ID_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<String> TOKEN_CONTEXT = new ThreadLocal<>();

    public static void setUserId(String userId) { USER_ID_CONTEXT.set(userId); }
    public static String getUserId() { return USER_ID_CONTEXT.get(); }
    public static void setUserToken(String token) { TOKEN_CONTEXT.set(token); }
    public static String getUserToken() { return TOKEN_CONTEXT.get(); }
    public static void clear() {
        USER_ID_CONTEXT.remove();
        TOKEN_CONTEXT.remove();
    }
}
