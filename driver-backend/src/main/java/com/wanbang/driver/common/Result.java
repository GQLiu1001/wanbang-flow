package com.wanbang.driver.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(200,"成功",data);
    }
    public static <T> Result<T> success() {
        return new Result<>(200,"成功",null);
    }
    public static <T> Result<T> fail(T data) {
        return new Result<>(400,"失败",data);
    }
    public static <T> Result<T> fail() {
        return new Result<>(400,"失败",null);
    }

}
