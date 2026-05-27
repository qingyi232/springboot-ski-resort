package com.ski.resort.service.impl;

import com.ski.resort.entity.FAQ;
import com.ski.resort.mapper.FAQMapper;
import com.ski.resort.service.ChatbotService;
import com.ski.resort.util.DeepSeekClient;
import com.ski.resort.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能客服服务实现类
 * 基于关键词匹配的FAQ问答系统
 *
 * @author XXX
 * @date 2025-10-24
 */
@Slf4j
@Service
public class ChatbotServiceImpl implements ChatbotService {

    @Autowired
    private FAQMapper faqMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DeepSeekClient deepSeekClient;

    private static final String CACHE_KEY_HOT_FAQ = "hot:faq";
    private static final int CACHE_EXPIRE_HOURS = 12;

    // 停用词列表（用于关键词提取）
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "的", "了", "和", "是", "在", "有", "我", "你", "他", "她", "它", "们",
            "这", "那", "哪", "什么", "怎么", "如何", "为什么", "吗", "呢", "啊",
            "吧", "呀", "嘛", "吗", "呢", "请问", "谢谢", "您好", "你好"
    ));

    /**
     * AI智能对话（使用DeepSeek API）
     * 这是真正的AI对话功能
     */
    @Override
    public String chatWithAI(String question) {
        log.info("AI客服收到问题：{}", question);

        if (question == null || question.trim().isEmpty()) {
            return "您好！我是飞跃滑雪场的AI智能客服，请问有什么可以帮助您的吗？😊";
        }

        try {
            // 调用DeepSeek API进行对话
            String aiResponse = deepSeekClient.chat(question);
            log.info("AI回复：{}", aiResponse);
            return aiResponse;
        } catch (Exception e) {
            log.error("AI对话失败", e);
            return "抱歉，AI客服暂时无法回答，您可以查看常见问题或联系人工客服 😊\n\n客服电话：400-xxxx-xxx";
        }
    }

    /**
     * FAQ智能问答（作为备用方案）
     * 算法：
     * 1. 提取用户问题的关键词
     * 2. 在FAQ知识库中匹配关键词
     * 3. 按匹配度排序返回最相关的FAQ
     */
    @Override
    public List<FAQ> answerQuestion(String question) {
        log.info("智能客服回答问题：{}", question);

        if (question == null || question.trim().isEmpty()) {
            return getHotFAQ(5);
        }

        try {
            // 1. 提取关键词
            List<String> keywords = extractKeywords(question);
            log.info("提取的关键词：{}", keywords);

            if (keywords.isEmpty()) {
                return getHotFAQ(5);
            }

            // 2. 获取所有FAQ
            List<FAQ> allFAQs = faqMapper.findAllEnabled();

            // 3. 计算匹配度并排序
            List<FAQWithScore> scoredFAQs = new ArrayList<>();
            for (FAQ faq : allFAQs) {
                int score = calculateMatchScore(faq, keywords);
                if (score > 0) {
                    scoredFAQs.add(new FAQWithScore(faq, score));
                }
            }

            // 4. 按匹配度降序排序，返回Top 5
            return scoredFAQs.stream()
                    .sorted((a, b) -> Integer.compare(b.score, a.score))
                    .limit(5)
                    .map(fs -> fs.faq)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("智能问答失败", e);
            return getHotFAQ(5);
        }
    }

    /**
     * 计算FAQ与关键词的匹配得分
     */
    private int calculateMatchScore(FAQ faq, List<String> keywords) {
        int score = 0;
        String faqText = (faq.getQuestion() + " " + faq.getKeywords()).toLowerCase();

        for (String keyword : keywords) {
            if (faqText.contains(keyword.toLowerCase())) {
                score += 10; // 每匹配一个关键词加10分
            }
        }

        // 热门FAQ额外加分
        if (faq.getIsHot() != null && faq.getIsHot() == 1) {
            score += 5;
        }

        return score;
    }

    /**
     * 提取问题关键词
     * 简化版：去除停用词，提取2-4字的词
     */
    @Override
    public List<String> extractKeywords(String question) {
        if (question == null || question.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> keywords = new ArrayList<>();
        String cleaned = question.trim().replaceAll("[,。!?！？、，；：]", " ");

        // 简单分词：提取包含的关键业务词汇
        String[] businessKeywords = {
            "雪具", "租赁", "归还", "押金", "损坏", "赔偿",
            "教练", "预约", "取消", "改期", "评价",
            "课程", "报名", "退课", "证书", "学费",
            "商品", "购买", "退货", "发货", "快递",
            "场地", "预订", "开放", "关闭", "维护",
            "会员", "充值", "积分", "优惠", "折扣",
            "账号", "密码", "注册", "登录", "修改",
            "支付", "退款", "发票", "微信", "支付宝",
            "时间", "价格", "费用", "收费", "营业"
        };

        for (String keyword : businessKeywords) {
            if (cleaned.contains(keyword)) {
                keywords.add(keyword);
            }
        }

        // 如果没有匹配到业务关键词，尝试简单分词
        if (keywords.isEmpty()) {
            String[] words = cleaned.split("\\s+");
            for (String word : words) {
                if (word.length() >= 2 && !STOP_WORDS.contains(word)) {
                    keywords.add(word);
                }
            }
        }

        return keywords.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 获取热门FAQ
     */
    @Override
    public List<FAQ> getHotFAQ(int limit) {
        try {
            Object cached = redisUtil.get(CACHE_KEY_HOT_FAQ);
            if (cached != null) {
                List<FAQ> faqList = (List<FAQ>) cached;
                return faqList.stream().limit(limit).collect(Collectors.toList());
            }

            List<FAQ> hotFAQ = faqMapper.findHotFAQ(limit);
            redisUtil.set(CACHE_KEY_HOT_FAQ, hotFAQ, CACHE_EXPIRE_HOURS * 3600);
            return hotFAQ;

        } catch (Exception e) {
            log.error("获取热门FAQ失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据分类获取FAQ
     */
    @Override
    public List<FAQ> getFAQByCategory(String category) {
        try {
            return faqMapper.findByCategory(category);
        } catch (Exception e) {
            log.error("根据分类获取FAQ失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取所有FAQ分类及数量
     */
    @Override
    public Map<String, Integer> getFAQCategories() {
        try {
            List<FAQ> allFAQs = faqMapper.findAllEnabled();
            Map<String, Integer> categories = new HashMap<>();

            // 预定义分类
            categories.put("雪具租赁", 0);
            categories.put("教练预约", 0);
            categories.put("课程报名", 0);
            categories.put("装备购买", 0);
            categories.put("场地预订", 0);
            categories.put("账号问题", 0);
            categories.put("支付问题", 0);
            categories.put("其他", 0);

            // 统计各分类数量
            for (FAQ faq : allFAQs) {
                String category = faq.getCategory();
                if (category != null) {
                    categories.put(category, categories.getOrDefault(category, 0) + 1);
                }
            }

            return categories;

        } catch (Exception e) {
            log.error("获取FAQ分类失败", e);
            return new HashMap<>();
        }
    }

    /**
     * 记录FAQ浏览
     */
    @Override
    public void viewFAQ(Long faqId) {
        try {
            faqMapper.incrementViewCount(faqId);
            log.info("FAQ浏览记录成功，ID：{}", faqId);
        } catch (Exception e) {
            log.error("记录FAQ浏览失败", e);
        }
    }

    /**
     * FAQ点赞
     */
    @Override
    public void likeFAQ(Long faqId) {
        try {
            faqMapper.incrementLikeCount(faqId);
            log.info("FAQ点赞成功，ID：{}", faqId);
        } catch (Exception e) {
            log.error("FAQ点赞失败", e);
        }
    }

    /**
     * 内部类：FAQ with Score（用于排序）
     */
    private static class FAQWithScore {
        FAQ faq;
        int score;

        FAQWithScore(FAQ faq, int score) {
            this.faq = faq;
            this.score = score;
        }
    }
}

