package com.cyneck.workflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表单实例实体
 *
 * @author Eric.Lee
 * @since 2021-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("act_fo_form_instance")
public class ActFoFormInstanceEntity implements Serializable {

    private static final long serialVersionUID = 3175178068947703062L;
    @TableId(type = IdType.AUTO)
    @TableField("ID_")
    private String id;

    @TableField("FORM_DEFINITION_ID_")
    private String formDefinitionId;

    @TableField("TASK_ID_")
    private String taskId;

    @TableField("PROC_INST_ID_")
    private String procInstId;

    @TableField("PROC_DEF_ID_")
    private String procDefId;

    @TableField("SUBMITTED_DATE_")
    private LocalDateTime submittedDate;

    @TableField("SUBMITTED_BY_")
    private String submittedBy;

    @TableField("FORM_VALUES_ID_")
    private String formValuesId;

    @TableField("TENANT_ID_")
    private String tenantId;

    @TableField("SCOPE_ID_")
    private String scopeId;

    @TableField("SCOPE_TYPE_")
    private String scopeType;

    @TableField("SCOPE_DEFINITION_ID_")
    private String scopeDefinitionId;


}
