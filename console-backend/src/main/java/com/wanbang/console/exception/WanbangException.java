package com.wanbang.console.exception;

import com.wanbang.console.enums.ResultCode;

public class WanbangException extends RuntimeException {
    private  Integer code;  // 错误码
    private String message; // 错误消息

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public WanbangException(ResultCode resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }
    public WanbangException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}