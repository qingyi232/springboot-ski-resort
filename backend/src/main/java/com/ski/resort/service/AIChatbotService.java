package com.ski.resort.service;

import com.ski.resort.dto.AIChatRequest;

/**
 * AI聊天机器人服务接口
 */
public interface AIChatbotService {
    
    /**
     * 处理用户消息
     * 
     * @param request 聊天请求
     * @return AI回复
     */
    String chat(AIChatRequest request);
}






