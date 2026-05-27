package com.ski.resort.dto;

import lombok.Data;

/**
 * AI聊天请求DTO
 */
@Data
public class AIChatRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户消息
     */
    private String message;
    
    /**
     * 会话ID（可选，用于保持对话上下文）
     */
    private String sessionId;
}






