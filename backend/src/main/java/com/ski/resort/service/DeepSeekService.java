package com.ski.resort.service;

import java.util.List;
import java.util.Map;

/**
 * DeepSeek AI服务接口
 */
public interface DeepSeekService {
    
    /**
     * 发送聊天请求
     * 
     * @param messages 消息列表
     * @return AI响应
     */
    String chat(List<Map<String, String>> messages);
    
    /**
     * 发送单条消息（简化版）
     * 
     * @param systemPrompt 系统提示词
     * @param userMessage 用户消息
     * @return AI响应
     */
    String chatSimple(String systemPrompt, String userMessage);
}





