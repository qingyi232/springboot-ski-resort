package com.ski.resort.service;

import com.ski.resort.entity.FAQ;

import java.util.List;
import java.util.Map;

/**
 * 智能客服服务接口
 * 提供AI对话和FAQ问答功能
 *
 * @author XXX
 * @date 2025-10-24
 */
public interface ChatbotService {

    /**
     * AI智能对话（使用DeepSeek API）
     * @param question 用户提问
     * @return AI回复内容
     */
    String chatWithAI(String question);

    /**
     * FAQ智能问答（根据用户问题匹配FAQ，作为备用）
     * @param question 用户提问
     * @return 匹配的FAQ列表（最多返回5条）
     */
    List<FAQ> answerQuestion(String question);

    /**
     * 获取热门FAQ
     * @param limit 数量限制
     * @return 热门FAQ列表
     */
    List<FAQ> getHotFAQ(int limit);

    /**
     * 根据分类获取FAQ
     * @param category 分类
     * @return FAQ列表
     */
    List<FAQ> getFAQByCategory(String category);

    /**
     * 获取所有FAQ分类
     * @return 分类列表及对应数量
     */
    Map<String, Integer> getFAQCategories();

    /**
     * 记录FAQ浏览
     * @param faqId FAQ ID
     */
    void viewFAQ(Long faqId);

    /**
     * FAQ点赞
     * @param faqId FAQ ID
     */
    void likeFAQ(Long faqId);

    /**
     * 提取问题关键词（用于智能匹配）
     * @param question 问题文本
     * @return 关键词列表
     */
    List<String> extractKeywords(String question);
}

