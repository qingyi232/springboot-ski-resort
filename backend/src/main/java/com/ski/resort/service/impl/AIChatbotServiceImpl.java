package com.ski.resort.service.impl;

import com.ski.resort.config.AIConfig;
import com.ski.resort.dto.AIChatRequest;
import com.ski.resort.service.AIChatbotService;
import com.ski.resort.service.DeepSeekService;
import com.ski.resort.service.SystemContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI聊天机器人服务实现
 */
@Slf4j
@Service
public class AIChatbotServiceImpl implements AIChatbotService {
    
    @Autowired
    private DeepSeekService deepSeekService;
    
    @Autowired
    private AIConfig aiConfig;
    
    @Autowired
    private SystemContextService systemContextService;
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public String chat(AIChatRequest request) {
        log.info("=== AI智能客服 ===");
        log.info("用户ID: {}, 会话ID: {}", request.getUserId(), request.getSessionId());
        log.info("用户消息: {}", request.getMessage());
        
        try {
            // 1. 获取历史对话（从Redis）
            List<Map<String, String>> messages = getChatHistory(request.getSessionId());
            
            // 2. 添加系统提示（如果是新会话）
            if (messages.isEmpty()) {
                // 获取系统真实数据上下文
                String systemContext = systemContextService.buildSystemContext();
                
                // 构建完整的系统提示
                String fullSystemPrompt = aiConfig.getChatbot().getSystemPrompt() + "\n\n" + systemContext;
                
                Map<String, String> systemMsg = new HashMap<>();
                systemMsg.put("role", "system");
                systemMsg.put("content", fullSystemPrompt);
                messages.add(systemMsg);
                
                log.info("系统上下文已构建，长度: {} 字符", systemContext.length());
            }
            
            // 3. 添加用户消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", request.getMessage());
            messages.add(userMsg);
            
            // 4. 调用AI
            String aiResponse = deepSeekService.chat(messages);
            
            // 5. 检查AI响应是否为错误信息，如果是则使用降级方案
            if (aiResponse.contains("服务暂时不可用") || aiResponse.contains("无法回答")) {
                log.warn("AI服务返回错误，使用降级方案");
                aiResponse = getFallbackResponse(request.getMessage());
            }
            
            // 6. 添加AI回复到历史
            Map<String, String> assistantMsg = new HashMap<>();
            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", aiResponse);
            messages.add(assistantMsg);
            
            // 7. 保存历史对话（保留最近的maxHistory条）
            saveChatHistory(request.getSessionId(), messages);
            
            log.info("AI回复: {}", aiResponse);
            return aiResponse;
            
        } catch (Exception e) {
            log.error("AI聊天服务异常: ", e);
            // 使用降级方案
            return getFallbackResponse(request.getMessage());
        }
    }
    
    /**
     * 降级响应方案（当AI服务不可用时）
     */
    private String getFallbackResponse(String userMessage) {
        String message = userMessage.toLowerCase();
        
        // 场地相关
        if (message.contains("场地") || message.contains("预约场地") || message.contains("开放时间")) {
            return "我们滑雪场有初级道、中级道和高级道三个场地可供预订。" +
                   "营业时间为：周一至周五 9:00-21:00，周末及节假日 8:00-22:00。" +
                   "场地租金根据难度等级不同，从80元/小时到200元/小时不等。" +
                   "您可以在场地预约页面查看详细信息并进行预订。";
        }
        
        // 教练相关
        if (message.contains("教练") || message.contains("预约教练") || message.contains("课程")) {
            return "我们有多位专业教练提供一对一或小班教学服务。" +
                   "课程包括单板和双板，涵盖初级、中级和高级各个难度等级。" +
                   "价格从300元到1200元不等，课程时长通常为2-3小时。" +
                   "您可以在滑雪课程页面查看所有课程详情和教练信息。";
        }
        
        // 雪具租赁相关
        if (message.contains("雪具") || message.contains("租赁") || message.contains("租") || message.contains("装备租")) {
            return "我们提供全套滑雪装备租赁服务，包括单板、双板、雪鞋、雪杖、头盔、护具等。" +
                   "租金从15元/小时到60元/小时不等，按小时计费。" +
                   "所有装备均定期维护保养，确保安全可靠。" +
                   "您可以在网站上下单，到店取货即可。";
        }
        
        // 装备购买相关
        if (message.contains("购买") || message.contains("商城") || message.contains("买装备")) {
            return "我们的装备商城提供各类专业滑雪装备销售，包括滑雪板、雪鞋、雪服、护具等。" +
                   "产品涵盖Burton、Salomon、K2等知名品牌。" +
                   "价格从几百元到数千元不等，均为正品保证。" +
                   "购买后支持全国配送，部分商品享受包邮服务。";
        }
        
        // 价格相关
        if (message.contains("价格") || message.contains("多少钱") || message.contains("费用")) {
            return "我们的价格体系如下：" +
                   "滑雪课程：300-1200元/次；" +
                   "场地租赁：80-200元/小时；" +
                   "雪具租赁：15-60元/小时；" +
                   "装备购买：根据品牌型号不同，价格在几百到数千元。" +
                   "具体价格请查看对应页面的详细信息。";
        }
        
        // 默认回复
        return "您好！我是飞跃滑雪场的智能客服。" +
               "我可以帮您了解以下信息：课程信息、教练预约、雪具租赁、装备购买、场地预订、营业时间、价格咨询等。" +
               "请告诉我您想了解什么，我会尽力为您解答。" +
               "如需更详细的帮助，也可以联系我们的人工客服：400-123-4567。";
    }
    
    /**
     * 获取聊天历史
     */
    private List<Map<String, String>> getChatHistory(String sessionId) {
        if (sessionId == null || redisTemplate == null) {
            return new ArrayList<>();
        }
        
        try {
            String key = "ai:chat:history:" + sessionId;
            Object historyObj = redisTemplate.opsForValue().get(key);
            if (historyObj instanceof List) {
                return (List<Map<String, String>>) historyObj;
            }
        } catch (Exception e) {
            log.warn("获取聊天历史失败: ", e);
        }
        
        return new ArrayList<>();
    }
    
    /**
     * 保存聊天历史
     */
    private void saveChatHistory(String sessionId, List<Map<String, String>> messages) {
        if (sessionId == null || redisTemplate == null) {
            return;
        }
        
        try {
            // 只保留最近的maxHistory条消息
            int maxHistory = aiConfig.getChatbot().getMaxHistory();
            if (messages.size() > maxHistory) {
                messages = messages.subList(messages.size() - maxHistory, messages.size());
            }
            
            String key = "ai:chat:history:" + sessionId;
            redisTemplate.opsForValue().set(key, messages, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("保存聊天历史失败: ", e);
        }
    }
}

