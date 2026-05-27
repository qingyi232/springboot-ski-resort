package com.ski.resort.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * FAQ常见问题实体类
 * 用于智能客服知识库
 *
 * @author XXX
 * @date 2025-10-24
 */
@Data
public class FAQ implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 问题分类（雪具租赁/教练预约/课程报名/装备购买/场地预订/账号问题/支付问题/其他） */
    private String category;

    /** 问题标题 */
    private String question;

    /** 问题答案 */
    private String answer;

    /** 关键词（用于匹配，逗号分隔） */
    private String keywords;

    /** 浏览次数 */
    private Integer viewCount;

    /** 点赞次数 */
    private Integer likeCount;

    /** 是否热门（0:否,1:是） */
    private Integer isHot;

    /** 状态（0:禁用,1:启用） */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除（0:未删除,1:已删除） */
    private Integer isDeleted;
}







