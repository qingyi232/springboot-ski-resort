-- ========================================
-- 飞跃滑雪场管理系统 - 完整数据库初始化脚本
-- 创建时间：2025-10-24
-- 说明：包含数据库创建、建表、初始数据
-- 可重复执行：会先删除旧数据库
-- ========================================

-- ========================================
-- 第一部分：创建数据库
-- ========================================

-- 删除旧数据库（如果存在）
DROP DATABASE IF EXISTS ski_resort;

-- 创建新数据库
CREATE DATABASE ski_resort CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE ski_resort;

-- ========================================
-- 第二部分：创建数据表（16张表）
-- ========================================

-- 1. 用户表
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    real_name VARCHAR(100) COMMENT '真实姓名',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT 'https://api.dicebear.com/7.x/avataaars/svg?seed=default' COMMENT '头像URL',
    gender TINYINT COMMENT '性别：0女，1男',
    birthday DATE COMMENT '生日',
    id_card VARCHAR(18) COMMENT '身份证号',
    role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin/user/coach',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用，1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    INDEX idx_username(username),
    INDEX idx_phone(phone),
    INDEX idx_role(role),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 雪具表
CREATE TABLE t_equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '雪具ID',
    equipment_code VARCHAR(50) NOT NULL UNIQUE COMMENT '雪具编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '雪具名称',
    equipment_type VARCHAR(50) NOT NULL COMMENT '类型：单板/双板/雪杖/头盔/雪镜',
    brand VARCHAR(100) COMMENT '品牌',
    model VARCHAR(100) COMMENT '型号',
    size VARCHAR(50) COMMENT '尺寸规格',
    rental_price DECIMAL(10,2) NOT NULL COMMENT '租赁价格（元/小时）',
    purchase_price DECIMAL(10,2) COMMENT '购入价格',
    purchase_date DATE COMMENT '购入日期',
    status TINYINT DEFAULT 1 COMMENT '状态：0维修中，1可用，2已租出',
    stock_quantity INT DEFAULT 1 COMMENT '库存数量',
    available_quantity INT DEFAULT 1 COMMENT '可用数量',
    image_url VARCHAR(255) COMMENT '图片URL',
    description TEXT COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_equipment_code(equipment_code),
    INDEX idx_type(equipment_type),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='雪具表';

-- 3. 教练表
CREATE TABLE t_coach (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教练ID',
    user_id BIGINT COMMENT '关联用户ID（可选）',
    coach_name VARCHAR(50) NOT NULL COMMENT '教练姓名',
    coach_code VARCHAR(50) UNIQUE COMMENT '教练编号',
    coach_level VARCHAR(20) NOT NULL COMMENT '等级：初级/中级/高级/专业',
    specialty VARCHAR(200) COMMENT '专长',
    experience_years INT DEFAULT 0 COMMENT '从业年限',
    certification VARCHAR(200) COMMENT '认证信息',
    hourly_rate DECIMAL(10,2) NOT NULL COMMENT '课时费（元/小时）',
    rating DECIMAL(3,2) DEFAULT 5.0 COMMENT '评分（1-5）',
    total_reviews INT DEFAULT 0 COMMENT '评价总数',
    total_students INT DEFAULT 0 COMMENT '累计学员数',
    avatar VARCHAR(255) COMMENT '头像',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    introduction TEXT COMMENT '个人简介',
    max_daily_hours INT DEFAULT 8 COMMENT '每日最大授课时长（小时）',
    status TINYINT DEFAULT 1 COMMENT '状态：0休假，1在职，2离职',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_user_id(user_id),
    INDEX idx_coach_code(coach_code),
    INDEX idx_status(status),
    INDEX idx_phone(phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练表';

-- 4. 课程表
CREATE TABLE t_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_code VARCHAR(50) NOT NULL UNIQUE COMMENT '课程编号',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_type VARCHAR(50) NOT NULL COMMENT '类型：初级入门/中级进阶/高级提升',
    coach_id BIGINT NOT NULL COMMENT '授课教练ID',
    coach_name VARCHAR(50) COMMENT '教练姓名',
    max_students INT NOT NULL COMMENT '最大学员数',
    current_students INT DEFAULT 0 COMMENT '当前学员数',
    course_price DECIMAL(10,2) NOT NULL COMMENT '课程价格',
    course_duration DECIMAL(10,2) NOT NULL COMMENT '课程时长（小时）',
    total_lessons INT DEFAULT 1 COMMENT '总节数',
    per_lesson_hours DECIMAL(10,2) DEFAULT NULL COMMENT '每节课时长（小时）',
    late_policy TINYINT DEFAULT 2 COMMENT '迟到规则：0课时作废不补，1可延期补课，2迟到15分钟内可正常上课',
    course_date DATE NOT NULL COMMENT '开课日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    venue_id BIGINT COMMENT '场地ID',
    venue_name VARCHAR(100) COMMENT '场地名称',
    difficulty VARCHAR(20) COMMENT '难度等级',
    description TEXT COMMENT '课程描述',
    image_url VARCHAR(255) COMMENT '课程图片',
    status TINYINT DEFAULT 1 COMMENT '状态：0未开始，1报名中，2进行中，3已结束',
    is_hot TINYINT DEFAULT 0 COMMENT '是否热门：0否，1是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_course_code(course_code),
    INDEX idx_coach_id(coach_id),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 5. 场地表
CREATE TABLE t_venue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '场地ID',
    venue_code VARCHAR(50) NOT NULL UNIQUE COMMENT '场地编号',
    venue_name VARCHAR(100) NOT NULL COMMENT '场地名称',
    venue_type VARCHAR(50) NOT NULL COMMENT '类型：雪道/练习场',
    difficulty_level VARCHAR(20) COMMENT '难度等级：初级/中级/高级',
    length INT COMMENT '长度（米）',
    width INT COMMENT '宽度（米）',
    max_capacity INT COMMENT '最大容纳人数',
    current_capacity INT DEFAULT 0 COMMENT '当前人数',
    rental_price DECIMAL(10,2) COMMENT '租赁价格（元/小时）',
    status TINYINT DEFAULT 1 COMMENT '状态：0维护中，1开放',
    image_url VARCHAR(255) COMMENT '场地图片',
    description TEXT COMMENT '场地描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_venue_code(venue_code),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地表';

-- 6. 商品表
CREATE TABLE t_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '商品编号',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    category VARCHAR(50) NOT NULL COMMENT '分类：滑雪装备/服装/配件',
    brand VARCHAR(100) COMMENT '品牌',
    model VARCHAR(100) COMMENT '型号',
    specification VARCHAR(200) COMMENT '规格',
    price DECIMAL(10,2) NOT NULL COMMENT '销售价格',
    cost_price DECIMAL(10,2) COMMENT '成本价格',
    stock_quantity INT DEFAULT 0 COMMENT '库存数量',
    sold_quantity INT DEFAULT 0 COMMENT '已售数量',
    min_stock INT DEFAULT 10 COMMENT '最小库存',
    image_url VARCHAR(255) COMMENT '图片URL',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0下架，1上架',
    is_hot TINYINT DEFAULT 0 COMMENT '是否热门',
    is_new TINYINT DEFAULT 0 COMMENT '是否新品',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_product_code(product_code),
    INDEX idx_category(category),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 7. 租赁订单表
CREATE TABLE t_rental_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    equipment_id BIGINT NOT NULL COMMENT '雪具ID',
    equipment_name VARCHAR(100) COMMENT '雪具名称',
    rental_start_time DATETIME NOT NULL COMMENT '租赁开始时间',
    rental_end_time DATETIME COMMENT '计划归还时间',
    actual_return_time DATETIME COMMENT '实际归还时间',
    rental_hours DECIMAL(10,2) COMMENT '租赁时长（小时）',
    rental_price DECIMAL(10,2) NOT NULL COMMENT '租赁单价',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    deposit DECIMAL(10,2) DEFAULT 0 COMMENT '押金',
    order_status TINYINT DEFAULT 0 COMMENT '订单状态：0待支付，1使用中，2已归还，3已取消',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态：0未支付，1已支付',
    payment_time DATETIME COMMENT '支付时间',
    payment_method VARCHAR(20) COMMENT '支付方式',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_order_no(order_no),
    INDEX idx_user_id(user_id),
    INDEX idx_order_status(order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁订单表';

-- 8. 销售订单表
CREATE TABLE t_sales_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    item_count INT DEFAULT 0 COMMENT '商品数量',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0待支付，1待发货，2已发货，3已完成，4已取消',
    order_status TINYINT DEFAULT 0 COMMENT '订单状态：0待支付，1待发货，2已发货，3已完成，4已取消',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态：0未支付，1已支付',
    payment_time DATETIME COMMENT '支付时间',
    payment_method VARCHAR(20) COMMENT '支付方式',
    shipping_status TINYINT DEFAULT 0 COMMENT '发货状态：0未发货，1已发货，2已收货',
    shipping_time DATETIME COMMENT '发货时间',
    shipping_address VARCHAR(255) COMMENT '收货地址',
    receiver_name VARCHAR(50) COMMENT '收货人',
    receiver_phone VARCHAR(20) COMMENT '收货电话',
    receiver_address VARCHAR(255) COMMENT '详细地址（冗余字段，同shipping_address）',
    express_company VARCHAR(50) COMMENT '快递公司',
    express_no VARCHAR(50) COMMENT '快递单号',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_order_no(order_no),
    INDEX idx_user_id(user_id),
    INDEX idx_order_status(order_status),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表';

-- 9. 销售订单详情表
CREATE TABLE t_sales_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '详情ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(100) COMMENT '商品名称',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id(order_id),
    INDEX idx_product_id(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单详情表';

-- 10. 教练预约表
CREATE TABLE t_coach_booking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    booking_no VARCHAR(50) NOT NULL UNIQUE COMMENT '预约号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    booking_for_other TINYINT DEFAULT 0 COMMENT '是否代他人预约：0自己，1代他人',
    student_name VARCHAR(50) COMMENT '实际上课人姓名',
    student_phone VARCHAR(20) COMMENT '实际上课人手机号',
    person_count INT DEFAULT 1 COMMENT '预约人数',
    coach_id BIGINT NOT NULL COMMENT '教练ID',
    coach_name VARCHAR(50) COMMENT '教练姓名',
    course_id BIGINT COMMENT '课程ID',
    course_name VARCHAR(100) COMMENT '课程名称',
    course_type VARCHAR(50) COMMENT '课程类型',
    booking_date DATE NOT NULL COMMENT '预约日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    hours DECIMAL(10,2) NOT NULL COMMENT '时长（小时）',
    hourly_rate DECIMAL(10,2) NOT NULL COMMENT '课时费',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '预约状态：0待确认，1已确认，2进行中，3已完成，4已取消',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态：0未支付，1已支付',
    payment_time DATETIME COMMENT '支付时间',
    payment_method VARCHAR(20) COMMENT '支付方式',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    cancel_time DATETIME COMMENT '取消时间',
    rating TINYINT COMMENT '评分（1-5）',
    comment TEXT COMMENT '评价内容',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_booking_no(booking_no),
    INDEX idx_user_id(user_id),
    INDEX idx_coach_id(coach_id),
    INDEX idx_course_id(course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练预约表';

-- 11. 场地预订表
CREATE TABLE t_venue_booking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预订ID',
    booking_no VARCHAR(50) NOT NULL UNIQUE COMMENT '预订号',
    venue_id BIGINT NOT NULL COMMENT '场地ID',
    venue_name VARCHAR(100) COMMENT '场地名称',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    people_count INT DEFAULT 1 COMMENT '预订人数',
    booking_date DATE NOT NULL COMMENT '预订日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    hours DECIMAL(10,2) NOT NULL COMMENT '时长（小时）',
    rental_price DECIMAL(10,2) NOT NULL COMMENT '租赁单价',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '预订状态：0待确认，1已确认，2使用中，3已完成，4已取消',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态：0未支付，1已支付',
    payment_time DATETIME COMMENT '支付时间',
    payment_method VARCHAR(20) COMMENT '支付方式',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    cancel_time DATETIME COMMENT '取消时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_booking_no(booking_no),
    INDEX idx_venue_id(venue_id),
    INDEX idx_user_id(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地预订表';

-- 12. 课程报名表
CREATE TABLE t_course_enrollment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报名ID',
    enrollment_no VARCHAR(50) NOT NULL UNIQUE COMMENT '报名号',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    course_name VARCHAR(100) COMMENT '课程名称',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    course_price DECIMAL(10,2) NOT NULL COMMENT '课程价格',
    enrollment_status TINYINT DEFAULT 0 COMMENT '报名状态：0待审核，1已通过，2学习中，3已完成',
    attended_lessons INT DEFAULT 0 COMMENT '已上课节数',
    remaining_lessons INT DEFAULT 0 COMMENT '剩余课节数',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态：0未支付，1已支付',
    payment_time DATETIME COMMENT '支付时间',
    payment_method VARCHAR(20) COMMENT '支付方式',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_enrollment_no(enrollment_no),
    INDEX idx_course_id(course_id),
    INDEX idx_user_id(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程报名表';

-- 13. 评价表
CREATE TABLE t_evaluation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    target_type VARCHAR(20) NOT NULL COMMENT '评价对象类型：coach/course/product',
    target_id BIGINT NOT NULL COMMENT '对象ID',
    target_name VARCHAR(100) COMMENT '对象名称',
    rating TINYINT NOT NULL COMMENT '评分（1-5）',
    comment TEXT COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_user_id(user_id),
    INDEX idx_target(target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 14. 会员卡表
CREATE TABLE t_member_card (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会员卡ID',
    card_no VARCHAR(50) NOT NULL UNIQUE COMMENT '会员卡号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    card_type VARCHAR(20) NOT NULL COMMENT '类型：month/quarter/year',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    discount_rate DECIMAL(3,2) DEFAULT 0.90 COMMENT '折扣率',
    start_date DATE NOT NULL COMMENT '生效日期',
    end_date DATE NOT NULL COMMENT '到期日期',
    status TINYINT DEFAULT 1 COMMENT '状态：0已过期，1使用中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_card_no(card_no),
    INDEX idx_user_id(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员卡表';

-- 15. 租赁明细表
CREATE TABLE t_rental_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    equipment_id BIGINT NOT NULL COMMENT '雪具ID',
    equipment_name VARCHAR(100) COMMENT '雪具名称',
    quantity INT NOT NULL COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id(order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁明细表';

-- 16. 雪具维修工单表
CREATE TABLE t_repair_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '工单ID',
    repair_no VARCHAR(50) NOT NULL UNIQUE COMMENT '工单编号',
    user_id BIGINT NOT NULL COMMENT '报修用户ID',
    user_name VARCHAR(50) COMMENT '报修用户姓名',
    equipment_id BIGINT NOT NULL COMMENT '雪具ID',
    equipment_name VARCHAR(100) COMMENT '雪具名称',
    fault_description TEXT NOT NULL COMMENT '故障描述',
    fault_images VARCHAR(500) COMMENT '故障图片URL（逗号分隔）',
    repair_type VARCHAR(50) DEFAULT '普通维修' COMMENT '维修类型：普通维修/紧急维修/保养',
    priority TINYINT DEFAULT 1 COMMENT '优先级：0低，1普通，2高，3紧急',
    status TINYINT DEFAULT 0 COMMENT '状态：0待处理，1处理中，2已完成，3已关闭',
    assigned_staff_id BIGINT COMMENT '指派工作人员ID',
    assigned_staff_name VARCHAR(50) COMMENT '指派工作人员姓名',
    repair_result TEXT COMMENT '维修结果说明',
    repair_cost DECIMAL(10,2) DEFAULT 0 COMMENT '维修费用',
    start_time DATETIME COMMENT '开始维修时间',
    finish_time DATETIME COMMENT '完成维修时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_repair_no(repair_no),
    INDEX idx_user_id(user_id),
    INDEX idx_equipment_id(equipment_id),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='雪具维修工单表';

-- 17. FAQ常见问题表
CREATE TABLE t_faq (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'FAQ ID',
    category VARCHAR(50) NOT NULL COMMENT '分类',
    question VARCHAR(200) NOT NULL COMMENT '问题',
    answer TEXT NOT NULL COMMENT '答案',
    keywords VARCHAR(200) COMMENT '关键词',
    is_hot TINYINT DEFAULT 0 COMMENT '是否热门：0否，1是',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_category(category),
    INDEX idx_is_hot(is_hot)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常见问题表';

-- ========================================
-- 第三部分：插入初始数据
-- ========================================

-- 1. 插入管理员和测试用户（9个，包含头像URL）
INSERT INTO t_user (username, password, real_name, phone, email, avatar, gender, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800138000', 'admin@skiresort.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Admin', 1, 'admin', 1),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', '13900139999', 'test@example.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=TestUser', 1, 'user', 1),
('coach1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张教练', '13800138001', 'coach1@skiresort.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhang', 1, 'coach', 1),
('coach2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李教练', '13800138002', 'coach2@skiresort.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Li', 1, 'coach', 1),
('coach3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王教练', '13800138003', 'coach3@skiresort.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Wang', 1, 'coach', 1),
('coach4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵教练', '13800138004', 'coach4@skiresort.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhao', 1, 'coach', 1),
('coach5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '刘教练', '13800138005', 'coach5@skiresort.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Liu', 1, 'coach', 1),
('staff1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈工作人员', '13800138006', 'staff1@skiresort.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Staff1', 1, 'staff', 1),
('staff2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙工作人员', '13800138007', 'staff2@skiresort.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Staff2', 0, 'staff', 1);

-- 2. 插入教练信息（5个）
INSERT INTO t_coach (user_id, coach_name, coach_code, coach_level, specialty, experience_years, certification, hourly_rate, rating, total_reviews, total_students, introduction, status) VALUES
(3, '张教练', 'COACH001', '高级', '单板滑雪、自由式滑雪', 8, '国家一级滑雪教练', 250.00, 4.8, 156, 320, '资深单板教练，擅长教授初学者快速入门', 1),
(4, '李教练', 'COACH002', '中级', '双板滑雪、初级教学', 5, '国家二级滑雪教练', 180.00, 4.6, 98, 210, '温柔耐心的双板教练', 1),
(5, '王教练', 'COACH003', '高级', '竞技滑雪、高级技巧', 10, '国家一级滑雪教练', 300.00, 4.9, 200, 450, '专业竞技教练', 1),
(6, '赵教练', 'COACH004', '中级', '儿童教学、基础入门', 6, '国家二级滑雪教练', 200.00, 4.7, 120, 280, '儿童滑雪专家', 1),
(7, '刘教练', 'COACH005', '初级', '零基础教学', 3, '国家三级滑雪教练', 150.00, 4.5, 80, 150, '耐心细致，适合零基础学员', 1);

-- 3. 插入雪具数据（10个）
INSERT INTO t_equipment (equipment_code, equipment_name, equipment_type, brand, model, size, rental_price, purchase_price, purchase_date, status, stock_quantity, available_quantity, image_url, description) VALUES
('EQ001', 'Burton Custom 单板', '单板', 'Burton', 'Custom', '158cm', 60.00, 2500.00, '2024-09-01', 1, 5, 5, 'https://picsum.photos/seed/eq1/800/600', '高端全能单板，适合中高级滑手'),
('EQ002', 'K2 Mindbender 双板', '双板', 'K2', 'Mindbender', '170cm', 50.00, 2000.00, '2024-09-01', 1, 8, 8, 'https://picsum.photos/seed/eq2/800/600', '全地形双板'),
('EQ003', 'Smith I/O MAG 雪镜', '雪镜', 'Smith', 'I/O MAG', '通用', 25.00, 800.00, '2024-09-01', 1, 10, 10, 'https://picsum.photos/seed/eq3/800/600', '快速换镜片系统'),
('EQ004', 'POC Fornix 头盔', '头盔', 'POC', 'Fornix', 'M', 30.00, 900.00, '2024-09-01', 1, 12, 12, 'https://picsum.photos/seed/eq4/800/600', '轻量化设计'),
('EQ005', 'Black Diamond 雪杖', '雪杖', 'Black Diamond', 'Compactor', '可调节', 15.00, 300.00, '2024-09-05', 1, 15, 15, 'https://picsum.photos/seed/eq5/800/600', '可折叠收纳'),
('EQ006', 'Rossignol Experience 双板', '双板', 'Rossignol', 'Experience', '168cm', 45.00, 1800.00, '2024-09-05', 1, 6, 6, 'https://picsum.photos/seed/eq6/800/600', '适合中级滑手'),
('EQ007', 'Burton Clash 单板', '单板', 'Burton', 'Clash', '155cm', 50.00, 1800.00, '2024-09-10', 1, 7, 7, 'https://picsum.photos/seed/eq7/800/600', '入门级单板'),
('EQ008', 'Oakley Flight Deck 雪镜', '雪镜', 'Oakley', 'Flight Deck', '通用', 25.00, 750.00, '2024-09-10', 1, 10, 10, 'https://picsum.photos/seed/eq8/800/600', '大视野设计'),
('EQ009', 'Giro Range 头盔', '头盔', 'Giro', 'Range', 'L', 25.00, 800.00, '2024-09-15', 1, 10, 10, 'https://picsum.photos/seed/eq9/800/600', '舒适透气'),
('EQ010', 'Salomon QST 双板', '双板', 'Salomon', 'QST', '172cm', 55.00, 2200.00, '2024-09-15', 1, 5, 5, 'https://picsum.photos/seed/eq10/800/600', '全山地双板');

-- 4. 插入商品数据（15个）
INSERT INTO t_product (product_code, product_name, category, brand, model, specification, price, cost_price, stock_quantity, sold_quantity, min_stock, image_url, description, status, is_hot, is_new) VALUES
('PROD001', 'Burton Custom X 单板', '滑雪装备', 'Burton', 'Custom X', '158cm', 4999.00, 3200.00, 15, 3, 5, 'https://picsum.photos/seed/prod1/800/600', '顶级竞技单板', 1, 1, 1),
('PROD002', 'K2 Mindbender 99Ti 双板', '滑雪装备', 'K2', 'Mindbender 99Ti', '170cm', 4599.00, 3000.00, 12, 2, 5, 'https://picsum.photos/seed/prod2/800/600', '全地形双板', 1, 1, 1),
('PROD003', 'Burton Step On 固定器', '滑雪装备', 'Burton', 'Step On', '通用', 2299.00, 1500.00, 20, 5, 8, 'https://picsum.photos/seed/prod3/800/600', '快速穿脱固定器', 1, 1, 0),
('PROD004', 'The North Face 滑雪服套装', '服装', 'The North Face', 'Freedom Suit', 'L', 2999.00, 1800.00, 30, 12, 10, 'https://picsum.photos/seed/prod4/800/600', '防水透气', 1, 1, 0),
('PROD005', 'Burton AK457 滑雪夹克', '服装', 'Burton', 'AK457', 'M', 3599.00, 2200.00, 20, 6, 8, 'https://picsum.photos/seed/prod5/800/600', '顶级防护', 1, 1, 1),
('PROD006', 'Columbia Bugaboo 滑雪裤', '服装', 'Columbia', 'Bugaboo', 'L', 899.00, 500.00, 50, 18, 15, 'https://picsum.photos/seed/prod6/800/600', '防水耐磨', 1, 1, 0),
('PROD007', 'Helly Hansen 保暖内衣', '服装', 'Helly Hansen', 'Lifa', 'L', 599.00, 300.00, 60, 25, 20, 'https://picsum.photos/seed/prod7/800/600', '速干保暖', 1, 1, 0),
('PROD008', 'Smith 4D MAG 雪镜', '配件', 'Smith', '4D MAG', '通用', 1899.00, 1200.00, 40, 15, 15, 'https://picsum.photos/seed/prod8/800/600', '顶级视野', 1, 1, 1),
('PROD009', 'POC Obex BC 头盔', '配件', 'POC', 'Obex BC', 'M', 1599.00, 1000.00, 35, 12, 12, 'https://picsum.photos/seed/prod9/800/600', 'MIPS防护', 1, 0, 1),
('PROD010', 'Black Diamond 雪杖', '配件', 'Black Diamond', 'Compactor', '可调节', 399.00, 200.00, 60, 20, 20, 'https://picsum.photos/seed/prod10/800/600', '可折叠收纳', 1, 0, 0),
('PROD011', 'Dakine 滑雪包', '配件', 'Dakine', 'Boot Pack', '50L', 699.00, 400.00, 50, 16, 18, 'https://picsum.photos/seed/prod11/800/600', '大容量', 1, 0, 0),
('PROD012', 'Smartwool 美丽诺羊毛袜', '服装', 'Smartwool', 'PhD', '均码', 189.00, 90.00, 100, 42, 30, 'https://picsum.photos/seed/prod12/800/600', '抗菌防臭', 1, 0, 0),
('PROD013', 'Outdoor Research 滑雪手套', '服装', 'Outdoor Research', 'Adrenaline', 'L', 499.00, 250.00, 80, 30, 25, 'https://picsum.photos/seed/prod13/800/600', '防水保暖', 1, 1, 0),
('PROD014', 'GoPro Hero 12 运动相机', '配件', 'GoPro', 'Hero 12', '标准版', 3299.00, 2500.00, 25, 8, 10, 'https://picsum.photos/seed/prod14/800/600', '记录精彩瞬间', 1, 1, 1),
('PROD015', 'Rossignol All Speed Pro 双板', '滑雪装备', 'Rossignol', 'All Speed Pro', '168cm', 3999.00, 2500.00, 10, 1, 5, 'https://picsum.photos/seed/prod15/800/600', '竞速双板', 1, 0, 1);

-- 5. 插入场地数据（6个）
INSERT INTO t_venue (venue_code, venue_name, venue_type, difficulty_level, length, width, max_capacity, current_capacity, rental_price, status, image_url, description) VALUES
('VENUE001', '初级雪道A区', '雪道', '初级', 500, 50, 100, 0, 0, 1, 'https://picsum.photos/seed/venue1/800/600', '适合初学者练习的缓坡雪道（包含在门票内）'),
('VENUE002', '初级雪道B区', '雪道', '初级', 600, 60, 120, 0, 0, 1, 'https://picsum.photos/seed/venue2/800/600', '宽敞的初级雪道（包含在门票内）'),
('VENUE003', '中级雪道C区', '雪道', '中级', 1000, 60, 80, 0, 0, 1, 'https://picsum.photos/seed/venue3/800/600', '中等难度雪道（包含在门票内）'),
('VENUE004', '中级雪道D区', '雪道', '中级', 1200, 70, 90, 0, 0, 1, 'https://picsum.photos/seed/venue4/800/600', '适合进阶练习（包含在门票内）'),
('VENUE005', '高级雪道E区', '雪道', '高级', 1500, 80, 50, 0, 0, 1, 'https://picsum.photos/seed/venue5/800/600', '陡峭雪道，适合高手（包含在门票内）'),
('VENUE006', '自由式公园', '练习场', NULL, 300, 150, 200, 0, 80.00, 1, 'https://picsum.photos/seed/venue6/800/600', '设有跳台、道具等（需额外付费）');

-- 6. 插入课程数据（8个）
INSERT INTO t_course (course_code, course_name, course_type, coach_id, coach_name, max_students, current_students, course_price, course_duration, total_lessons, per_lesson_hours, late_policy, course_date, start_time, end_time, venue_id, venue_name, difficulty, image_url, description, status, is_hot) VALUES
('COURSE001', '单板零基础入门班', '初级入门', 1, '张教练', 10, 0, 800.00, 4.0, 2, 2.0, 2, '2025-11-15', '09:00:00', '13:00:00', 1, '初级雪道A区', '初级', 'https://picsum.photos/seed/snowboard1/800/600', '从零开始学习单板滑雪', 1, 1),
('COURSE002', '双板初级体验课', '初级入门', 2, '李教练', 12, 0, 600.00, 3.0, 1, 3.0, 2, '2025-11-16', '14:00:00', '17:00:00', 2, '初级雪道B区', '初级', 'https://picsum.photos/seed/ski2/800/600', '双板滑雪基础教学', 1, 1),
('COURSE003', '单板进阶技巧班', '中级进阶', 1, '张教练', 8, 0, 1200.00, 6.0, 3, 2.0, 1, '2025-11-20', '09:00:00', '15:00:00', 3, '中级雪道C区', '中级', 'https://picsum.photos/seed/snowboard3/800/600', '提升单板技术', 1, 1),
('COURSE004', '双板中级提升课', '中级进阶', 2, '李教练', 10, 0, 1000.00, 5.0, 2, 2.5, 1, '2025-11-22', '10:00:00', '15:00:00', 4, '中级雪道D区', '中级', 'https://picsum.photos/seed/ski4/800/600', '双板中级技术训练', 1, 0),
('COURSE005', '自由式单板特训', '高级提升', 1, '张教练', 6, 0, 1800.00, 8.0, 4, 2.0, 0, '2025-11-25', '09:00:00', '17:00:00', 6, '自由式公园', '高级', 'https://picsum.photos/seed/freestyle5/800/600', '自由式动作教学', 1, 1),
('COURSE006', '儿童滑雪启蒙班', '初级入门', 4, '赵教练', 15, 0, 1500.00, 12.0, 6, 2.0, 1, '2025-12-01', '09:00:00', '17:00:00', 1, '初级雪道A区', '初级', 'https://picsum.photos/seed/kids6/800/600', '专为儿童设计', 1, 1),
('COURSE007', '竞技滑雪强化班', '高级提升', 3, '王教练', 5, 0, 2500.00, 10.0, 5, 2.0, 0, '2025-12-05', '08:00:00', '18:00:00', 5, '高级雪道E区', '高级', 'https://picsum.photos/seed/racing7/800/600', '竞技技巧强化训练', 1, 0),
('COURSE008', '双板平行式转弯课', '中级进阶', 2, '李教练', 8, 0, 1100.00, 5.0, 2, 2.5, 2, '2025-12-10', '14:00:00', '19:00:00', 3, '中级雪道C区', '中级', 'https://picsum.photos/seed/parallel8/800/600', '学习平行式转弯技巧', 1, 1);

-- 7. 插入FAQ数据（55条）
INSERT INTO t_faq (category, question, answer, keywords, is_hot) VALUES
-- 雪具租赁相关（10条）
('雪具租赁', '如何租赁雪具？', '您可以在首页点击"雪具租赁"，选择您需要的雪具类型，填写租赁时间和押金，提交订单后完成支付即可。', '雪具,租赁,怎么租', 1),
('雪具租赁', '雪具租赁需要押金吗？', '是的，为保障雪具安全，租赁时需要缴纳押金。押金金额根据雪具类型不同而不同，归还雪具且无损坏后，押金将全额退还。', '押金,需要', 1),
('雪具租赁', '雪具损坏了怎么办？', '如果雪具在使用过程中损坏，请及时联系工作人员。轻微磨损不收费，严重损坏将从押金中扣除赔偿金额。', '损坏,赔偿', 1),
('雪具租赁', '租赁时间怎么计算？', '租赁时间从取走雪具开始计算，归还雪具时结束。我们提供按小时和按天两种计费方式。', '时间,计算', 0),
('雪具租赁', '可以提前归还雪具吗？', '可以的，您可以随时归还雪具。但租赁费用按实际租赁时间计算，最少按1小时计算。', '归还,提前', 0),
('雪具租赁', '雪具租赁包含哪些装备？', '基础套餐包含雪板、雪鞋、雪杖。您也可以额外租赁头盔、护具、雪镜等防护装备。', '包含,装备', 1),
('雪具租赁', '不会滑雪可以租雪具吗？', '可以的。但我们建议初学者先报名滑雪课程，在教练指导下学习。', '不会,新手', 0),
('雪具租赁', '雪具尺寸怎么选择？', '雪具尺寸需要根据您的身高、体重、滑雪水平来选择。工作人员会根据您的情况推荐合适的尺寸。', '尺寸,选择', 0),
('雪具租赁', '可以在线预约雪具吗？', '可以的，您可以通过我们的网站或APP提前预约雪具，到达后可直接取用。', '预约,提前', 0),
('雪具租赁', '租赁雪具可以更换吗？', '如果雪具使用不舒适或发现质量问题，可以在租赁后30分钟内免费更换一次。', '更换,不合适', 0),

-- 教练预约相关（8条）
('教练预约', '如何预约教练？', '您可以在"教练预约"页面浏览教练信息，选择合适的教练和时间段，提交预约申请并完成支付。', '预约,教练', 1),
('教练预约', '教练费用怎么收取？', '教练费用按课时收取，不同等级的教练收费不同。初级教练约150元/小时，中级教练约200元/小时，高级教练约300元/小时。', '费用,价格', 1),
('教练预约', '可以取消教练预约吗？', '可以的。提前24小时取消，全额退款；提前12小时取消，退款50%；12小时内取消不予退款。', '取消,退款', 1),
('教练预约', '一对一和小组课有什么区别？', '一对一教学更有针对性，进步更快。小组课费用较低，适合朋友一起学习。', '一对一,小组', 0),
('教练预约', '教练都有哪些资质？', '我们的教练都持有国家滑雪协会颁发的教练员证书，并定期参加培训。', '资质,证书', 0),
('教练预约', '怎么选择合适的教练？', '可以根据教练的专长、等级、学员评价来选择。初学者建议选择耐心细致的初中级教练。', '选择,合适', 0),
('教练预约', '教练课程包含雪具租赁吗？', '教练费用仅包含教学服务，不包含雪具租赁。如需租赁雪具，请另行租赁。', '包含,雪具', 0),
('教练预约', '预约了教练可以改时间吗？', '可以的。至少提前12小时申请改期，每次预约可免费改期一次。', '改期,改时间', 0),

-- 课程报名相关（7条）
('课程报名', '有哪些滑雪课程？', '我们提供入门班、进阶班、高级班三种难度的课程，以及单板、双板专项课程。', '课程,有哪些', 1),
('课程报名', '课程费用是多少？', '入门班：800元起；进阶班：1200元起；高级班：1800元起。具体费用根据课程时长而定。', '费用,价格', 1),
('课程报名', '课程可以退费吗？', '开课前7天申请退课，扣除10%手续费后退还；开课前3天申请，退还50%；开课后不予退费。', '退费,退课', 1),
('课程报名', '完成课程能学会滑雪吗？', '入门班能让您掌握基本滑行技巧，可以在初级道独立滑雪；进阶班能掌握转弯、刹车等技巧。', '学会,效果', 0),
('课程报名', '课程时间怎么安排？', '课程为期2周，每周5节课，每节课2小时。具体上课时间根据您的选择确定。', '时间,安排', 0),
('课程报名', '课程人数有限制吗？', '为保证教学质量，每个班级限制人数。入门班最多15人，进阶班最多12人，高级班最多8人。', '人数,限制', 0),
('课程报名', '课程包含考试吗？', '课程结束时有结业考核，通过考核可获得滑雪协会颁发的等级证书。', '考试,证书', 0),

-- 装备购买相关（6条）
('装备购买', '可以在雪场买滑雪装备吗？', '可以的，我们提供单板、双板、雪服、护具等全套装备销售。商品保证正品。', '购买,买装备', 1),
('装备购买', '装备质量有保障吗？', '我们的装备均为知名品牌正品，提供正规发票和质保服务。如有质量问题，7天内可退换货。', '质量,保障', 1),
('装备购买', '装备可以退货吗？', '全新未使用的装备7天内可无理由退货，需保持包装完整。', '退货,换货', 0),
('装备购买', '购买装备有优惠吗？', '我们定期推出促销活动。会员享受9折优惠，购买套装有额外折扣。', '优惠,打折', 0),
('装备购买', '装备可以快递吗？', '可以的，我们支持全国包邮（偏远地区除外）。一般3-7天送达。', '快递,邮寄', 0),
('装备购买', '怎么选择合适的装备？', '建议根据您的滑雪水平、使用频率和预算来选择。我们的工作人员可为您提供专业建议。', '选择,推荐', 0),

-- 场地预订相关（5条）
('场地预订', '如何预订场地？', '在"场地管理"页面可以查看场地信息和可用时间，选择合适的场地和时段，提交预订申请并完成支付。', '预订,场地', 1),
('场地预订', '场地费用是多少？', '场地费用按时段收取，工作日白天200元/小时，周末节假日300元/小时。', '费用,价格', 1),
('场地预订', '场地开放时间是？', '雪场开放时间：工作日9:00-17:00，周末和节假日8:30-18:00。夜场17:30-21:00。', '时间,营业', 1),
('场地预订', '可以取消场地预订吗？', '提前24小时取消，全额退款；提前12小时取消，退款70%；12小时内取消不予退款。', '取消,退款', 0),
('场地预订', '团体活动如何预订？', '10人以上团体请至少提前3天联系客服预订，享受团队优惠价格。', '团体,团建', 0),

-- 账号问题相关（7条）
('账号问题', '如何注册账号？', '点击"注册"按钮，填写用户名、密码、手机号即可完成注册。', '注册,账号', 1),
('账号问题', '忘记密码怎么办？', '点击"忘记密码"，输入注册手机号，通过短信验证码即可重置密码。', '忘记密码,找回', 1),
('账号问题', '怎么修改个人信息？', '登录后进入"个人中心"，点击"编辑资料"即可修改昵称、头像等信息。', '修改,个人信息', 0),
('账号问题', '可以注销账号吗？', '可以的。进入"账号安全"页面，申请注销账号。注销前请确保无未完成订单。', '注销,删除账号', 0),
('账号问题', '会员有什么特权？', '会员享受装备购买9折优惠、优先预订场地和教练、专属客服等特权。', '会员,特权', 0),
('账号问题', '怎么绑定微信？', '进入"账号安全"页面，点击"绑定微信"，扫描二维码完成绑定。', '绑定,微信', 0),
('账号问题', '账号被盗了怎么办？', '立即联系客服（电话：400-xxxx-xxx）冻结账号，修改密码。', '被盗,盗号', 0),

-- 支付问题相关（6条）
('支付问题', '支持哪些支付方式？', '我们支持微信支付、支付宝、银联卡、现金等多种支付方式。', '支付,付款', 1),
('支付问题', '支付失败怎么办？', '支付失败可能是网络问题或余额不足。请检查网络和账户余额后重试。', '支付失败,失败', 1),
('支付问题', '可以申请退款吗？', '根据不同业务规定，符合退款条件的订单可申请退款。退款将原路返回，到账时间为3-7个工作日。', '退款,退钱', 1),
('支付问题', '可以开发票吗？', '可以的。支付完成后在订单详情页申请发票，填写发票抬头和税号。', '发票,开票', 0),
('支付问题', '有余额充值功能吗？', '暂不支持余额充值。每次消费请选择第三方支付方式。', '充值,余额', 0),
('支付问题', '支付安全吗？', '我们使用SSL加密传输，支付信息经过银行级别加密处理，所有支付均通过正规第三方支付平台完成。', '安全,可靠', 0),

-- 其他问题（6条）
('其他', '雪场地址在哪里？', '飞跃滑雪场位于XX市XX区XX路123号。自驾可导航"飞跃滑雪场"。', '地址,位置', 1),
('其他', '有停车场吗？', '有的，雪场提供免费停车场，车位充足。', '停车,车位', 0),
('其他', '有餐厅吗？', '雪场内设有自助餐厅和快餐区，提供中西式简餐、热饮、小吃等。', '餐厅,吃饭', 0),
('其他', '有储物柜吗？', '有的，雪场提供储物柜出租服务，20元/天。', '储物柜,存放', 0),
('其他', '儿童可以滑雪吗？', '4岁以上儿童可在家长陪同下滑雪，强烈建议参加儿童滑雪课程。', '儿童,小孩', 0),
('其他', '怎么联系客服？', '客服电话：400-xxxx-xxx（9:00-21:00）；微信公众号留言；网站在线客服。', '客服,联系', 1);

-- 更新FAQ统计（模拟浏览量和点赞）
UPDATE t_faq SET view_count = FLOOR(RAND() * 1000 + 100), like_count = FLOOR(RAND() * 200 + 20) WHERE is_hot = 1;
UPDATE t_faq SET view_count = FLOOR(RAND() * 500 + 50), like_count = FLOOR(RAND() * 100 + 10) WHERE is_hot = 0;

-- 10. 租赁订单表测试数据（6条）
INSERT INTO t_rental_order (order_no, user_id, equipment_id, equipment_name, rental_start_time, rental_end_time, actual_return_time, rental_hours, rental_price, total_amount, deposit, order_status, payment_status, payment_time, payment_method) VALUES
('RO202501250001', 2, 1, 'Burton Custom 单板', '2025-01-20 09:00:00', '2025-01-20 15:00:00', '2025-01-20 14:30:00', 6, 60.00, 360.00, 500.00, 2, 1, '2025-01-20 08:45:00', '微信支付'),
('RO202501250002', 2, 2, 'K2 Mindbender 双板', '2025-01-22 10:00:00', '2025-01-22 17:00:00', NULL, 7, 50.00, 350.00, 500.00, 1, 1, '2025-01-22 09:30:00', '支付宝'),
('RO202501250003', 2, 3, 'Smith I/O MAG 雪镜', '2025-01-23 08:00:00', '2025-01-23 18:00:00', NULL, 10, 25.00, 250.00, 200.00, 1, 1, '2025-01-23 07:45:00', '微信支付'),
('RO202501250004', 2, 4, 'Oakley Flight Deck 雪镜', '2025-01-25 09:00:00', '2025-01-25 16:00:00', NULL, 7, 20.00, 140.00, 200.00, 0, 0, NULL, NULL),
('RO202501250005', 2, 1, 'Burton Custom 单板', '2025-01-15 10:00:00', '2025-01-15 17:00:00', '2025-01-15 16:45:00', 7, 60.00, 420.00, 500.00, 2, 1, '2025-01-15 09:30:00', '现金'),
('RO202501250006', 2, 2, 'K2 Mindbender 双板', '2025-01-18 11:00:00', '2025-01-18 18:00:00', '2025-01-18 17:30:00', 7, 50.00, 350.00, 500.00, 2, 1, '2025-01-18 10:30:00', '支付宝');

-- 11. 教练预约测试数据（10条）
INSERT INTO t_coach_booking (booking_no, user_id, user_name, coach_id, coach_name, course_id, course_name, course_type, booking_date, start_time, end_time, hours, hourly_rate, total_amount, status, payment_status, payment_time, payment_method, rating, comment) VALUES
('CB202511010001', 2, '测试用户', 1, '张教练', 1, '单板零基础入门班', '初级入门', '2025-11-08', '09:00:00', '11:00:00', 2.00, 250.00, 500.00, 3, 1, '2025-11-01 08:30:00', '微信支付', 5, '教练非常专业，很耐心！'),
('CB202511010002', 2, '测试用户', 1, '张教练', 3, '单板进阶技巧班', '中级进阶', '2025-11-10', '14:00:00', '17:00:00', 3.00, 250.00, 750.00, 3, 1, '2025-11-02 10:00:00', '支付宝', 5, '进步很快，非常满意！'),
('CB202511010003', 2, '测试用户', 1, '张教练', 1, '单板零基础入门班', '初级入门', '2025-11-15', '09:00:00', '13:00:00', 4.00, 250.00, 1000.00, 1, 1, '2025-11-03 15:00:00', '微信支付', NULL, NULL),
('CB202511010004', 2, '测试用户', 2, '李教练', 2, '双板初级体验课', '初级入门', '2025-11-05', '10:00:00', '12:00:00', 2.00, 180.00, 360.00, 3, 1, '2025-10-28 09:00:00', '微信支付', 4, '讲解很清楚'),
('CB202511010005', 2, '测试用户', 2, '李教练', 4, '双板中级提升课', '中级进阶', '2025-11-12', '15:00:00', '18:00:00', 3.00, 180.00, 540.00, 1, 1, '2025-11-01 16:00:00', '支付宝', NULL, NULL),
('CB202511010006', 2, '测试用户', 3, '王教练', 7, '竞技滑雪强化班', '高级提升', '2025-11-06', '08:00:00', '12:00:00', 4.00, 300.00, 1200.00, 3, 1, '2025-10-30 14:00:00', '微信支付', 5, '高手就是高手！'),
('CB202511010007', 2, '测试用户', 4, '赵教练', 6, '儿童滑雪启蒙班', '初级入门', '2025-11-18', '09:00:00', '17:00:00', 8.00, 200.00, 1600.00, 0, 0, NULL, NULL, NULL, NULL),
('CB202511010008', 2, '测试用户', 1, '张教练', 3, '单板进阶技巧班', '中级进阶', '2025-11-20', '09:00:00', '15:00:00', 6.00, 250.00, 1500.00, 0, 0, NULL, NULL, NULL, NULL),
('CB202511010009', 2, '测试用户', 2, '李教练', 4, '双板中级提升课', '中级进阶', '2025-11-22', '10:00:00', '15:00:00', 5.00, 180.00, 900.00, 0, 0, NULL, NULL, NULL, NULL),
('CB202511010010', 2, '测试用户', 1, '张教练', 5, '自由式单板特训', '高级提升', '2025-11-25', '09:00:00', '17:00:00', 8.00, 250.00, 2000.00, 2, 1, '2025-11-04 11:00:00', '微信支付', NULL, NULL);

-- 12. 场地预订测试数据（8条）
INSERT INTO t_venue_booking (booking_no, venue_id, venue_name, user_id, user_name, people_count, booking_date, start_time, end_time, hours, rental_price, total_amount, status, payment_status, payment_time, payment_method) VALUES
('VB202511010001', 1, '初级雪道A区', 2, '测试用户', 2, '2025-11-08', '09:00:00', '12:00:00', 3.00, 300.00, 900.00, 3, 1, '2025-11-01 08:00:00', '微信支付'),
('VB202511010002', 2, '初级雪道B区', 2, '测试用户', 3, '2025-11-10', '14:00:00', '18:00:00', 4.00, 300.00, 1200.00, 3, 1, '2025-11-02 10:00:00', '支付宝'),
('VB202511010003', 3, '中级雪道C区', 2, '测试用户', 1, '2025-11-15', '10:00:00', '15:00:00', 5.00, 400.00, 2000.00, 1, 1, '2025-11-03 14:00:00', '微信支付'),
('VB202511010004', 4, '中级雪道D区', 2, '测试用户', 4, '2025-11-18', '09:00:00', '17:00:00', 8.00, 400.00, 3200.00, 1, 1, '2025-11-04 09:00:00', '微信支付'),
('VB202511010005', 1, '初级雪道A区', 2, '测试用户', 1, '2025-11-20', '13:00:00', '18:00:00', 5.00, 300.00, 1500.00, 0, 0, NULL, NULL),
('VB202511010006', 5, '高级雪道E区', 2, '测试用户', 2, '2025-11-22', '09:00:00', '12:00:00', 3.00, 500.00, 1500.00, 0, 0, NULL, NULL),
('VB202511010007', 6, '自由式公园', 2, '测试用户', 3, '2025-11-25', '14:00:00', '18:00:00', 4.00, 600.00, 2400.00, 0, 0, NULL, NULL),
('VB202511010008', 3, '中级雪道C区', 2, '测试用户', 1, '2025-11-28', '10:00:00', '16:00:00', 6.00, 400.00, 2400.00, 0, 0, NULL, NULL);

-- 13. 销售订单测试数据（5条）
INSERT INTO t_sales_order (order_no, user_id, user_name, item_count, receiver_name, receiver_phone, receiver_address, shipping_address, total_amount, discount_amount, actual_amount, status, shipping_status, payment_method, payment_time, express_company, express_no, remark) VALUES
('SO202511010001', 2, '测试用户', 1, '张三', '13900139999', '北京市朝阳区建国路88号', '北京市朝阳区建国路88号', 2500.00, 0.00, 2500.00, 2, 2, '微信支付', '2025-11-01 10:00:00', '顺丰速运', 'SF1234567890', '商品：Burton Custom 单板 x1'),
('SO202511010002', 2, '测试用户', 1, '李四', '13900139999', '上海市浦东新区世纪大道100号', '上海市浦东新区世纪大道100号', 2000.00, 0.00, 2000.00, 2, 2, '支付宝', '2025-11-02 14:00:00', '圆通快递', 'YT0987654321', '商品：K2 Mindbender 双板 x1'),
('SO202511010003', 2, '测试用户', 2, '王五', '13900139999', '广州市天河区珠江新城', '广州市天河区珠江新城', 3500.00, 350.00, 3150.00, 1, 1, '微信支付', '2025-11-03 09:00:00', '顺丰速运', 'SF2345678901', '商品：滑雪服套装 x1, 护具套装 x1'),
('SO202511010004', 2, '测试用户', 2, '赵六', '13900139999', '深圳市南山区科技园', '深圳市南山区科技园', 1500.00, 0.00, 1500.00, 1, 0, '支付宝', '2025-11-04 16:00:00', NULL, NULL, '商品：专业滑雪头盔 x1, 高端雪镜 x1'),
('SO202511010005', 2, '测试用户', 3, '孙七', '13900139999', '成都市锦江区春熙路', '成都市锦江区春熙路', 4500.00, 450.00, 4050.00, 0, 0, '微信支付', '2025-11-05 11:00:00', NULL, NULL, '商品：单板滑雪板 x1, 固定器 x1, 雪靴 x1');

-- ========================================
-- 第四部分：数据验证
-- ========================================

SELECT '========================================' AS '';
SELECT '✅ 数据库初始化完成！' AS '状态';
SELECT '========================================' AS '';

SELECT '数据表统计' AS '检查项', COUNT(*) AS '数量' FROM information_schema.tables WHERE table_schema = 'ski_resort'
UNION ALL SELECT '用户数据', COUNT(*) FROM t_user
UNION ALL SELECT '教练数据', COUNT(*) FROM t_coach
UNION ALL SELECT '雪具数据', COUNT(*) FROM t_equipment
UNION ALL SELECT '商品数据', COUNT(*) FROM t_product
UNION ALL SELECT '场地数据', COUNT(*) FROM t_venue
UNION ALL SELECT '课程数据', COUNT(*) FROM t_course
UNION ALL SELECT '租赁订单数据', COUNT(*) FROM t_rental_order
UNION ALL SELECT '教练预约数据', COUNT(*) FROM t_coach_booking
UNION ALL SELECT '场地预订数据', COUNT(*) FROM t_venue_booking
UNION ALL SELECT '销售订单数据', COUNT(*) FROM t_sales_order
UNION ALL SELECT 'FAQ数据', COUNT(*) FROM t_faq;

-- ========================================
-- 数据修复：修正租赁订单中错误的状态码
-- ========================================
-- 租赁订单的状态码定义：0待支付，1使用中，2已归还，3已取消
-- 将错误设置为 4 的状态码修正为 3（已取消）
UPDATE t_rental_order SET order_status = 3 WHERE order_status = 4 AND is_deleted = 0;

SELECT '========================================' AS '';
SELECT '测试账号（密码均为：123456）' AS '说明';
SELECT '========================================' AS '';
SELECT username AS '用户名', real_name AS '姓名', role AS '角色' FROM t_user WHERE is_deleted = 0;

-- ========================================
-- 完成！
-- ========================================

