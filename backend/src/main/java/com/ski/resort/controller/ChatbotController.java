package com.ski.resort.controller;

import com.ski.resort.common.Result;
import com.ski.resort.entity.FAQ;
import com.ski.resort.service.ChatbotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能客服Controller
 *
 * @author XXX
 * @date 2025-10-24
 */
@Slf4j
@Api(tags = "智能客服")
@RestController
@RequestMapping("/api/v1/chatbot")
@CrossOrigin
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * AI智能对话接口（使用DeepSeek API）
     */
    @ApiOperation("AI智能对话")
    @PostMapping("/chat")
    public Result<Map<String, Object>> chatWithAI(@ApiParam("用户问题") @RequestBody Map<String, String> request) {
        String question = request.get("question");
        log.info("AI客服收到问题：{}", question);

        if (question == null || question.trim().isEmpty()) {
            return Result.error("问题不能为空");
        }

        // 调用AI对话
        String aiResponse = chatbotService.chatWithAI(question);
        
        Map<String, Object> result = new HashMap<>();
        result.put("answer", aiResponse);
        result.put("type", "ai"); // 标记为AI回复
        
        return Result.success("回答成功", result);
    }

    /**
     * FAQ智能问答接口（备用）
     */
    @ApiOperation("FAQ智能问答")
    @PostMapping("/ask")
    public Result<List<FAQ>> askQuestion(@ApiParam("用户问题") @RequestBody Map<String, String> request) {
        String question = request.get("question");
        log.info("收到用户问题：{}", question);

        if (question == null || question.trim().isEmpty()) {
            return Result.error("问题不能为空");
        }

        List<FAQ> answers = chatbotService.answerQuestion(question);
        return Result.success("回答成功", answers);
    }

    /**
     * 获取热门FAQ
     */
    @ApiOperation("获取热门FAQ")
    @GetMapping("/hot")
    public Result<List<FAQ>> getHotFAQ(@ApiParam("数量") @RequestParam(defaultValue = "10") int limit) {
        List<FAQ> hotFAQ = chatbotService.getHotFAQ(limit);
        return Result.success("获取成功", hotFAQ);
    }

    /**
     * 根据分类获取FAQ
     */
    @ApiOperation("根据分类获取FAQ")
    @GetMapping("/category/{category}")
    public Result<List<FAQ>> getFAQByCategory(@ApiParam("分类") @PathVariable String category) {
        List<FAQ> faqs = chatbotService.getFAQByCategory(category);
        return Result.success("获取成功", faqs);
    }

    /**
     * 获取所有FAQ分类
     */
    @ApiOperation("获取FAQ分类")
    @GetMapping("/categories")
    public Result<Map<String, Integer>> getFAQCategories() {
        Map<String, Integer> categories = chatbotService.getFAQCategories();
        return Result.success("获取成功", categories);
    }

    /**
     * 记录FAQ浏览
     */
    @ApiOperation("记录FAQ浏览")
    @PostMapping("/view/{faqId}")
    public Result<Void> viewFAQ(@ApiParam("FAQ ID") @PathVariable Long faqId) {
        chatbotService.viewFAQ(faqId);
        return Result.success("记录成功");
    }

    /**
     * FAQ点赞
     */
    @ApiOperation("FAQ点赞")
    @PostMapping("/like/{faqId}")
    public Result<Void> likeFAQ(@ApiParam("FAQ ID") @PathVariable Long faqId) {
        chatbotService.likeFAQ(faqId);
        return Result.success("点赞成功");
    }

    /**
     * 提取关键词（测试接口）
     */
    @ApiOperation("提取问题关键词")
    @PostMapping("/keywords")
    public Result<List<String>> extractKeywords(@ApiParam("问题文本") @RequestBody Map<String, String> request) {
        String question = request.get("question");
        List<String> keywords = chatbotService.extractKeywords(question);
        return Result.success("提取成功", keywords);
    }
}

