package com.ski.resort.service.impl;

import com.ski.resort.entity.*;
import com.ski.resort.mapper.*;
import com.ski.resort.service.SystemContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统上下文服务实现
 */
@Slf4j
@Service
public class SystemContextServiceImpl implements SystemContextService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private EquipmentMapper equipmentMapper;
    
    @Autowired
    private VenueMapper venueMapper;
    
    @Autowired
    private CoachMapper coachMapper;
    
    @Override
    public String buildSystemContext() {
        StringBuilder context = new StringBuilder();
        
        context.append("【飞跃滑雪场系统真实数据】\n\n");
        
        // 1. 课程信息
        context.append("=== 课程列表 ===\n");
        List<Course> courses = courseMapper.selectAll();
        for (Course course : courses) {
            context.append(String.format(
                "课程名称：%s，难度：%s，教练：%s，价格：%.0f元，时长：%.1f小时，描述：%s\n",
                course.getCourseName(),
                course.getDifficulty(),
                course.getCoachName(),
                course.getCoursePrice(),
                course.getCourseDuration(),
                course.getDescription()
            ));
        }
        context.append("\n");
        
        // 2. 雪具租赁信息
        context.append("=== 雪具租赁 ===\n");
        List<Equipment> equipments = equipmentMapper.selectAll();
        for (Equipment equipment : equipments) {
            context.append(String.format(
                "雪具名称：%s，类型：%s，租金：%.0f元/小时，库存：%d件，描述：%s\n",
                equipment.getEquipmentName(),
                equipment.getEquipmentType(),
                equipment.getRentalPrice(),
                equipment.getStockQuantity(),
                equipment.getDescription()
            ));
        }
        context.append("\n");
        
        // 3. 装备商城
        context.append("=== 装备商城（销售） ===\n");
        List<Product> products = productMapper.selectAll();
        for (Product product : products) {
            context.append(String.format(
                "商品名称：%s，品牌：%s，价格：%.0f元，库存：%d件，规格：%s\n",
                product.getProductName(),
                product.getBrand(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getSpecification()
            ));
        }
        context.append("\n");
        
        // 4. 场地信息
        context.append("=== 场地预约 ===\n");
        List<Venue> venues = venueMapper.selectAll();
        for (Venue venue : venues) {
            context.append(String.format(
                "场地名称：%s，难度：%s，租金：%.0f元/小时，容量：%d人，描述：%s\n",
                venue.getVenueName(),
                venue.getDifficultyLevel(),
                venue.getRentalPrice(),
                venue.getMaxCapacity(),
                venue.getDescription()
            ));
        }
        context.append("\n");
        
        // 5. 教练信息
        context.append("=== 教练团队 ===\n");
        List<Coach> coaches = coachMapper.selectAll();
        for (Coach coach : coaches) {
            context.append(String.format(
                "教练姓名：%s，等级：%s，专长：%s，从业年限：%d年，评分：%.1f分，简介：%s\n",
                coach.getCoachName(),
                coach.getCoachLevel() != null ? coach.getCoachLevel() : "未知",
                coach.getSpecialty() != null ? coach.getSpecialty() : "全能",
                coach.getExperienceYears() != null ? coach.getExperienceYears() : 0,
                coach.getRating() != null ? coach.getRating().doubleValue() : 5.0,
                coach.getIntroduction() != null ? coach.getIntroduction() : ""
            ));
        }
        context.append("\n");
        
        // 6. 营业信息
        context.append("=== 营业信息 ===\n");
        context.append("营业时间：周一至周五 9:00-21:00，周末及节假日 8:00-22:00\n");
        context.append("联系电话：400-123-4567\n");
        context.append("邮箱：info@skiresort.com\n");
        context.append("地址：飞跃滑雪场\n\n");
        
        // 7. 预约说明
        context.append("=== 预约流程 ===\n");
        context.append("1. 教练预约：选择课程 → 选择时间 → 确认支付 → 等待教练确认\n");
        context.append("2. 场地预订：选择场地 → 选择时间和人数 → 确认支付\n");
        context.append("3. 雪具租赁：选择雪具 → 选择租赁时长 → 下单支付 → 到店取货\n");
        context.append("4. 装备购买：浏览商城 → 加入购物车 → 结算支付 → 等待发货\n\n");
        
        // 8. 支付方式
        context.append("=== 支付方式 ===\n");
        context.append("支持微信支付、支付宝、银行卡支付\n\n");
        
        // 9. 退改政策
        context.append("=== 退改政策 ===\n");
        context.append("课程预约：开课前24小时可免费取消，24小时内取消收取50%费用\n");
        context.append("场地预订：使用前2小时可免费取消\n");
        context.append("雪具租赁：取货前可随时取消\n");
        context.append("装备购买：未发货前可申请退款，已发货需联系客服\n");
        
        return context.toString();
    }
}

