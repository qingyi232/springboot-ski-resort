package com.ski.resort.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ski.resort.config.AIConfig;
import com.ski.resort.service.DeepSeekService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek AI服务实现
 */
@Slf4j
@Service
public class DeepSeekServiceImpl implements DeepSeekService {
    
    @Autowired
    private AIConfig aiConfig;
    
    private WebClient webClient;
    
    /**
     * 懒加载WebClient
     */
    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = WebClient.builder()
                    .baseUrl(aiConfig.getDeepseek().getApiUrl())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiConfig.getDeepseek().getApiKey())
                    .build();
        }
        return webClient;
    }
    
    @Override
    public String chat(List<Map<String, String>> messages) {
        try {
            log.info("===  调用DeepSeek API ===");
            log.info("消息数量: {}", messages.size());
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getDeepseek().getModel());
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", aiConfig.getDeepseek().getMaxTokens());
            requestBody.put("temperature", aiConfig.getDeepseek().getTemperature());
            
            log.info("请求体: {}", JSON.toJSONString(requestBody));
            
            // 发送请求
            String responseBody = getWebClient()
                    .post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            log.info("DeepSeek响应: {}", responseBody);
            
            // 解析响应
            JSONObject jsonResponse = JSON.parseObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                String content = message.getString("content");
                
                // 清理Markdown格式符号，确保纯文本输出
                content = cleanMarkdownSymbols(content);
                
                log.info("AI回复: {}", content);
                return content;
            }
            
            log.warn("DeepSeek API响应为空");
            return "抱歉，我现在无法回答您的问题，请稍后再试。";
            
        } catch (Exception e) {
            log.error("调用DeepSeek API失败: ", e);
            return "抱歉，服务暂时不可用，请稍后再试。";
        }
    }
    
    @Override
    public String chatSimple(String systemPrompt, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 添加系统提示
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);
        
        // 添加用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        return chat(messages);
    }
    
    /**
     * 清理Markdown格式符号，确保纯文本输出
     */
    private String cleanMarkdownSymbols(String text) {
        if (text == null) {
            return null;
        }
        
        // 移除Markdown格式符号
        text = text.replaceAll("\\*\\*", "");  // 移除粗体 **
        text = text.replaceAll("\\*", "");      // 移除斜体 *
        text = text.replaceAll("^#+\\s+", "");  // 移除标题 #
        text = text.replaceAll("\\n#+\\s+", "\n"); // 移除行首标题
        text = text.replaceAll("^-\\s+", "");   // 移除列表符号 -
        text = text.replaceAll("\\n-\\s+", "\n"); // 移除行首列表
        text = text.replaceAll("^\\*\\s+", "");   // 移除列表符号 *
        text = text.replaceAll("\\n\\*\\s+", "\n"); // 移除行首列表
        text = text.replaceAll("^\\d+\\.\\s+", "");  // 移除数字列表 1.
        text = text.replaceAll("\\n\\d+\\.\\s+", "\n"); // 移除行首数字列表
        text = text.replaceAll("`", "");        // 移除代码符号 `
        text = text.replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1"); // 移除链接格式，保留文字
        text = text.replaceAll(">\\s+", "");    // 移除引用符号 >
        text = text.replaceAll("~~", "");       // 移除删除线 ~~
        text = text.replaceAll("__", "");       // 移除下划线 __
        
        return text.trim();
    }
}

