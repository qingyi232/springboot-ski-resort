package com.ski.resort.common;

/**
 * 系统常量
 *
 * @author XXX
 * @date 2025-10-08
 */
public class Constants {

    /**
     * 用户角色
     */
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    public static final String ROLE_COACH = "coach";
    public static final String ROLE_STAFF = "staff";

    /**
     * 用户状态
     */
    public static final Integer USER_STATUS_DISABLED = 0;
    public static final Integer USER_STATUS_NORMAL = 1;

    /**
     * 删除状态
     */
    public static final Integer NOT_DELETED = 0;
    public static final Integer DELETED = 1;

    /**
     * 性别
     */
    public static final Integer GENDER_FEMALE = 0;
    public static final Integer GENDER_MALE = 1;

    /**
     * 雪具状态
     */
    public static final Integer EQUIPMENT_STATUS_MAINTENANCE = 0;  // 维修中
    public static final Integer EQUIPMENT_STATUS_AVAILABLE = 1;    // 可用
    public static final Integer EQUIPMENT_STATUS_RENTED = 2;       // 已租出
    public static final Integer EQUIPMENT_STATUS_SCRAPPED = 3;     // 已报废

    /**
     * 订单状态
     */
    public static final Integer ORDER_STATUS_UNPAID = 0;        // 待支付
    public static final Integer ORDER_STATUS_PENDING = 1;       // 待取货/待确认
    public static final Integer ORDER_STATUS_IN_USE = 2;        // 使用中/进行中
    public static final Integer ORDER_STATUS_COMPLETED = 3;     // 已完成
    public static final Integer ORDER_STATUS_CANCELLED = 4;     // 已取消
    public static final Integer ORDER_STATUS_OVERTIME = 5;      // 超时

    /**
     * 支付状态
     */
    public static final Integer PAYMENT_STATUS_UNPAID = 0;      // 未支付
    public static final Integer PAYMENT_STATUS_PAID = 1;        // 已支付

    /**
     * 支付方式
     */
    public static final String PAYMENT_METHOD_ALIPAY = "alipay";
    public static final String PAYMENT_METHOD_WECHAT = "wechat";
    public static final String PAYMENT_METHOD_CASH = "cash";

    /**
     * 教练等级
     */
    public static final String COACH_LEVEL_JUNIOR = "初级";
    public static final String COACH_LEVEL_INTERMEDIATE = "中级";
    public static final String COACH_LEVEL_SENIOR = "高级";
    public static final String COACH_LEVEL_PROFESSIONAL = "专业";

    /**
     * 教练状态
     */
    public static final Integer COACH_STATUS_VACATION = 0;      // 休假
    public static final Integer COACH_STATUS_ACTIVE = 1;        // 在职
    public static final Integer COACH_STATUS_RESIGNED = 2;      // 离职

    /**
     * 课程类型
     */
    public static final String COURSE_TYPE_BEGINNER = "初级入门";
    public static final String COURSE_TYPE_INTERMEDIATE = "中级进阶";
    public static final String COURSE_TYPE_ADVANCED = "高级提升";
    public static final String COURSE_TYPE_COMPETITION = "竞技训练";

    /**
     * 课程状态
     */
    public static final Integer COURSE_STATUS_NOT_STARTED = 0;  // 未开始
    public static final Integer COURSE_STATUS_ENROLLING = 1;    // 报名中
    public static final Integer COURSE_STATUS_IN_PROGRESS = 2;  // 进行中
    public static final Integer COURSE_STATUS_COMPLETED = 3;    // 已结束
    public static final Integer COURSE_STATUS_CANCELLED = 4;    // 已取消

    /**
     * 会员卡类型
     */
    public static final String CARD_TYPE_MONTH = "month";
    public static final String CARD_TYPE_QUARTER = "quarter";
    public static final String CARD_TYPE_YEAR = "year";

    /**
     * 会员卡状态
     */
    public static final Integer CARD_STATUS_EXPIRED = 0;        // 已过期
    public static final Integer CARD_STATUS_ACTIVE = 1;         // 使用中
    public static final Integer CARD_STATUS_FROZEN = 2;         // 已冻结

    /**
     * 场地类型
     */
    public static final String VENUE_TYPE_SLOPE = "雪道";
    public static final String VENUE_TYPE_PRACTICE = "练习场";
    public static final String VENUE_TYPE_REST = "休息区";
    public static final String VENUE_TYPE_OTHER = "其他";

    /**
     * 难度等级
     */
    public static final String DIFFICULTY_BEGINNER = "初级";
    public static final String DIFFICULTY_INTERMEDIATE = "中级";
    public static final String DIFFICULTY_ADVANCED = "高级";
    public static final String DIFFICULTY_PROFESSIONAL = "专业";

    /**
     * 场地状态
     */
    public static final Integer VENUE_STATUS_MAINTENANCE = 0;   // 维护中
    public static final Integer VENUE_STATUS_OPEN = 1;          // 开放
    public static final Integer VENUE_STATUS_CLOSED = 2;        // 关闭

    /**
     * 日志类型
     */
    public static final String LOG_TYPE_LOGIN = "login";
    public static final String LOG_TYPE_OPERATION = "operation";
    public static final String LOG_TYPE_ERROR = "error";

    /**
     * 默认分页参数
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer MAX_PAGE_SIZE = 100;

    /**
     * Redis Key前缀
     */
    public static final String REDIS_KEY_USER = "user:";
    public static final String REDIS_KEY_TOKEN = "token:";
    public static final String REDIS_KEY_CODE = "code:";
    public static final String REDIS_KEY_EQUIPMENT = "equipment:";
    public static final String REDIS_KEY_COURSE = "course:";

    /**
     * Token过期时间（秒）
     */
    public static final Long TOKEN_EXPIRATION = 604800L;  // 7天

    /**
     * 验证码过期时间（秒）
     */
    public static final Long CODE_EXPIRATION = 300L;  // 5分钟

    /**
     * 默认头像
     */
    public static final String DEFAULT_AVATAR = "/default-avatar.png";

    /**
     * 文件上传路径
     */
    public static final String UPLOAD_PATH = "/uploads/";

    /**
     * 允许上传的图片格式
     */
    public static final String[] ALLOWED_IMAGE_TYPES = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};

    /**
     * 最大文件大小（MB）
     */
    public static final Integer MAX_FILE_SIZE = 10;

}



