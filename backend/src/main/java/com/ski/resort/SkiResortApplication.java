package com.ski.resort;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 飞跃滑雪场管理系统 - 主启动类
 * 
 * @author XXX
 * @date 2025-10-08
 */
@SpringBootApplication
@MapperScan("com.ski.resort.mapper")
@EnableCaching
public class SkiResortApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkiResortApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("飞跃滑雪场管理系统启动成功！");
        System.out.println("API文档地址：http://localhost:8080/api/doc.html");
        System.out.println("========================================\n");
    }

}



