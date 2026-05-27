package com.ski.resort.service;

/**
 * 系统上下文服务接口
 * 为AI提供系统真实数据
 */
public interface SystemContextService {
    
    /**
     * 构建完整的系统上下文信息
     * 包括课程、装备、场地、价格等所有真实数据
     * 
     * @return 系统上下文字符串
     */
    String buildSystemContext();
}






