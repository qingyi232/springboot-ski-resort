package com.ski.resort.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ski.resort.config.DeepSeekConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * DeepSeek AI客户端
 * 用于调用DeepSeek API进行智能对话
 *
 * @author XXX
 * @date 2025-10-24
 */
@Slf4j
@Component
public class DeepSeekClient {

    @Autowired
    private DeepSeekConfig config;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 系统Prompt - 定义AI的角色和行为规则
    private static final String SYSTEM_PROMPT = 
        "你是飞跃滑雪场的智能客服助手，专门为用户提供关于滑雪场和系统使用的帮助。\n" +
        "\n" +
        "【你的职责】\n" +
        "1. 回答关于飞跃滑雪场的各类问题（场地、设施、营业时间、价格等）\n" +
        "2. 指导用户如何使用滑雪场管理系统（注册、租赁雪具、预约教练、报名课程等）\n" +
        "3. 提供滑雪相关的基础知识和建议\n" +
        "4. 解答用户在使用系统过程中遇到的问题\n" +
        "\n" +
        "【系统功能介绍】\n" +
        "- 雪具租赁：用户可以在线租赁单板、双板、雪鞋、头盔等装备\n" +
        "- 教练预约：可以预约不同等级的滑雪教练进行一对一或小组教学\n" +
        "- 课程报名：提供入门班、进阶班、高级班等系统课程\n" +
        "- 装备购买：在线购买滑雪装备，支持配送\n" +
        "- 场地预订：预订不同难度的雪道和场地\n" +
        "- 会员管理：支持办理会员卡，享受优惠和特权\n" +
        "\n" +
        "【回答规则】\n" +
        "1. 回答要简洁明了、友好热情，使用中文\n" +
        "2. 可以适当使用emoji表情符号（如😊❄️⛷️🏂等）增强亲和力\n" +
        "3. 不要使用Markdown格式符号（#、*、**、-、>等）\n" +
        "4. 使用数字序号（1. 2. 3.）或换行来组织信息，不使用列表符号\n" +
        "5. 如果涉及价格，说明仅供参考，具体以系统显示为准\n" +
        "6. 如果不确定或超出职责范围，建议用户联系人工客服\n" +
        "7. 回答长度控制在200字以内，简洁为主\n" +
        "\n" +
        "【滑雪场基本信息】\n" +
        "- 名称：飞跃滑雪场\n" +
        "- 营业时间：工作日9:00-17:00，周末8:30-18:00，夜场17:30-21:00\n" +
        "- 支付方式：支持微信、支付宝、银联卡\n" +
        "- 客服电话：400-xxxx-xxx\n" +
        "\n" +
        "现在开始为用户服务吧！记住：友好、专业、简洁！";

    /**
     * 调用DeepSeek API进行对话
     *
     * @param userMessage 用户消息
     * @return AI回复
     */
    public String chat(String userMessage) {
        try {
            // 1. 构建请求体
            Map<String, Object> requestBody = buildRequestBody(userMessage);
            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            log.info("DeepSeek API请求：{}", jsonRequest);

            // 2. 发送HTTP请求
            URL url = new URL(config.getApiUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000); // 30秒超时
            conn.setReadTimeout(30000);

            // 3. 写入请求数据
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 4. 读取响应
            int responseCode = conn.getResponseCode();
            log.info("DeepSeek API响应码：{}", responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                log.info("DeepSeek API响应：{}", response.toString());

                // 5. 解析响应
                String aiResponse = parseResponse(response.toString());
                
                // 6. 格式化回复（去除Markdown符号）
                String formattedResponse = formatResponse(aiResponse);
                
                return formattedResponse;

            } else {
                // 读取错误信息
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
                br.close();
                
                log.error("DeepSeek API错误：{}", errorResponse.toString());
                return "抱歉，AI客服暂时无法回答，请稍后再试或联系人工客服 😊";
            }

        } catch (Exception e) {
            log.error("调用DeepSeek API失败", e);
            return "抱歉，AI客服遇到了问题，请稍后再试或联系人工客服 😊\n\n客服电话：400-xxxx-xxx";
        }
    }

    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", config.getModel());
        requestBody.put("max_tokens", config.getMaxTokens());
        requestBody.put("temperature", config.getTemperature());

        // 构建消息列表
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 系统消息（定义AI角色）
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", SYSTEM_PROMPT);
        messages.add(systemMessage);

        // 用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        return requestBody;
    }

    /**
     * 解析API响应，提取AI回复内容
     */
    private String parseResponse(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode choices = root.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            JsonNode firstChoice = choices.get(0);
            JsonNode message = firstChoice.path("message");
            String content = message.path("content").asText();
            return content;
        }
        return "抱歉，我没能理解您的问题，请换个方式提问 😊";
    }

    /**
     * 格式化AI回复
     * 去除Markdown特殊符号，保留文字和emoji
     */
    private String formatResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return "抱歉，我没能生成回复，请重新提问 😊";
        }

        String formatted = response;

        // 去除Markdown标题符号 (#、##、###等)
        formatted = formatted.replaceAll("#{1,6}\\s+", "");

        // 去除Markdown粗体/斜体符号 (**text**, *text*)
        formatted = formatted.replaceAll("\\*\\*([^*]+)\\*\\*", "$1");
        formatted = formatted.replaceAll("\\*([^*]+)\\*", "$1");

        // 去除Markdown列表符号 (-, *, +开头)
        formatted = formatted.replaceAll("(?m)^[\\-\\*\\+]\\s+", "• ");

        // 去除Markdown引用符号 (>)
        formatted = formatted.replaceAll("(?m)^>\\s+", "");

        // 去除Markdown代码块符号 (```、`)
        formatted = formatted.replaceAll("```[\\w]*\\n?", "");
        formatted = formatted.replaceAll("`([^`]+)`", "$1");

        // 去除多余的空行（保留单个换行）
        formatted = formatted.replaceAll("\\n{3,}", "\n\n");

        // 去除首尾空白
        formatted = formatted.trim();

        // 如果回复过长，适当截断
        if (formatted.length() > 500) {
            formatted = formatted.substring(0, 497) + "...";
        }

        return formatted;
    }

    /**
     * 测试API连接
     */
    public boolean testConnection() {
        try {
            String response = chat("你好");
            return response != null && !response.contains("暂时无法回答");
        } catch (Exception e) {
            log.error("测试DeepSeek API连接失败", e);
            return false;
        }
    }
}







