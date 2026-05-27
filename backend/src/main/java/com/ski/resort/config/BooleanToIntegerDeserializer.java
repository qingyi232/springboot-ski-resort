package com.ski.resort.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 布尔值转整数的反序列化器
 * 用于将前端的 true/false 转换为数据库的 1/0
 */
public class BooleanToIntegerDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // 获取原始值
        String text = p.getText();
        
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // 处理布尔值字符串
        if ("true".equalsIgnoreCase(text)) {
            return 1;
        } else if ("false".equalsIgnoreCase(text)) {
            return 0;
        }
        
        // 处理数字
        try {
            int value = Integer.parseInt(text);
            return value > 0 ? 1 : 0;
        } catch (NumberFormatException e) {
            // 默认返回0
            return 0;
        }
    }
}





