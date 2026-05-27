package com.ski.resort.common;

/**
 * 响应状态码枚举
 */
public enum ResultCode {
    
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 失败
     */
    ERROR(500, "操作失败"),
    
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),
    
    /**
     * 禁止访问
     */
    FORBIDDEN(403, "没有权限访问"),
    
    /**
     * 未找到
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),
    
    /**
     * 业务异常
     */
    BUSINESS_ERROR(1001, "业务异常"),
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统错误"),
    
    /**
     * 验证失败
     */
    VALIDATE_FAILED(400, "参数验证失败"),
    
    /**
     * 操作失败
     */
    FAILED(500, "操作失败"),
    
    /**
     * 参数为空
     */
    PARAM_IS_NULL(400, "参数不能为空"),
    
    /**
     * 用户不存在
     */
    USER_NOT_FOUND(404, "用户不存在"),
    
    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS(400, "用户已存在"),
    
    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(400, "用户名或密码错误"),
    
    /**
     * 用户已被禁用
     */
    USER_DISABLED(403, "用户已被禁用"),
    
    /**
     * 旧密码错误
     */
    OLD_PASSWORD_ERROR(400, "旧密码错误");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
