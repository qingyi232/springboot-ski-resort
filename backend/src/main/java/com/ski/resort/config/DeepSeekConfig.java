package com.ski.resort.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek AI配置
 *
 * @author XXX
 * @date 2025-10-24
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {

    /**
     * API密钥
     */
    private String apiKey = "sk-3b30d9ad425d450ba28d54324d98450a";

    /**
     * API地址
     */
    private String apiUrl = "https://api.deepseek.com/v1/chat/completions";

    /**
     * 模型名称
     */
    private String model = "deepseek-chat";

    /**
     * 最大Token数
     */
    private Integer maxTokens = 2000;

    /**
     * 温度参数（0-1，越高越随机）
     */
    private Double temperature = 0.7;
}







