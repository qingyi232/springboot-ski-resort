package com.ski.resort.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 用于配置静态资源访问
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源处理器
     * 将 /uploads/** 请求映射到实际的 uploads/ 目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        
        // 如果需要支持绝对路径，可以使用：
        // String uploadPath = new File("uploads").getAbsolutePath();
        // registry.addResourceHandler("/uploads/**")
        //         .addResourceLocations("file:" + uploadPath + "/");
    }
}
