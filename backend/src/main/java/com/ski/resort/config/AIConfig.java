package com.ski.resort.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIConfig {
    
    /**
     * DeepSeek配置
     */
    private DeepSeekConfig deepseek = new DeepSeekConfig();
    
    /**
     * 推荐系统配置
     */
    private RecommendationConfig recommendation = new RecommendationConfig();
    
    /**
     * 智能客服配置
     */
    private ChatbotConfig chatbot = new ChatbotConfig();
    
    @Data
    public static class DeepSeekConfig {
        private String apiKey;
        private String apiUrl;
        private String model;
        private Integer maxTokens;
        private Double temperature;
    }
    
    @Data
    public static class RecommendationConfig {
        private Boolean enabled;
        private Integer cacheTtl;
        private Integer maxRecommendations;
    }
    
    @Data
    public static class ChatbotConfig {
        private Boolean enabled;
        private String systemPrompt;
        private Integer maxHistory;
    }
}





