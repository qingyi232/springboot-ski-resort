package com.ski.resort.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RepairOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String repairNo;

    private Long userId;

    private String userName;

    private Long equipmentId;

    private String equipmentName;

    private String faultDescription;

    private String faultImages;

    private String repairType;

    private Integer priority;

    /**
     * 状态：0待处理，1处理中，2已完成，3已关闭
     */
    private Integer status;

    private Long assignedStaffId;

    private String assignedStaffName;

    private String repairResult;

    private BigDecimal repairCost;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @JsonIgnore
    private Integer isDeleted;
}
