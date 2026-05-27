package com.ski.resort.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 代码生成工具类
 *
 * @author XXX
 * @date 2025-10-08
 */
public class CodeGenerator {

    /**
     * 生成订单号
     */
    public static String generateOrderNo(String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 10000));
        return prefix + timestamp + String.format("%04d", Integer.parseInt(random));
    }

    /**
     * 生成雪具编号
     */
    public static String generateEquipmentCode() {
        return generateOrderNo("EQ");
    }

    /**
     * 生成教练编号
     */
    public static String generateCoachCode() {
        return generateOrderNo("CO");
    }

    /**
     * 生成商品编号
     */
    public static String generateProductCode() {
        return generateOrderNo("PD");
    }

    /**
     * 生成课程编号
     */
    public static String generateCourseCode() {
        return generateOrderNo("CR");
    }

    /**
     * 生成场地编号
     */
    public static String generateVenueCode() {
        return generateOrderNo("VN");
    }

    /**
     * 生成租赁订单号
     */
    public static String generateRentalOrderNo() {
        return generateOrderNo("RO");
    }

}



