package com.cyneck.workflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 流程任务表单数据variable
 *
 * @author Eric.Lee
 * @since 2021-02-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("act_ru_variable")
public class ActRuVariableEntity implements Serializable {

    private static final long serialVersionUID = 4427745027576530962L;
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private String id;

    @TableField("rev")
    private Integer rev;

    @TableField("type")
    private String type;

    @TableField("name")
    private String name;

    @TableField("executionId")
    private String executionId;

    @TableField("procInstId")
    private String procInstId;

    @TableField("taskId")
    private String taskId;

    @TableField("scopeId")
    private String scopeId;

    @TableField("subScopeId")
    private String subScopeId;

    @TableField("scopeType")
    private String scopeType;

    @TableField("byteArrayId")
    private String byteArrayId;

    @TableField("doubleI")
    private BigDecimal doubleI;

    @TableField("longI")
    private Long longI;

    @TableField("text")
    private String text;

    @TableField("text2")
    private String text2;

}
