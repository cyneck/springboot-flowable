package com.cyneck.workflow.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * <p>Description: 任务执行</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:37
 **/
@Data
@ApiModel(value = "ExecuteTaskRequest", description = "任务执行req")
public class ExecuteTaskReq implements Serializable {


    @ApiModelProperty(value = "执行id", required = true)
    private String taskId;

    @ApiModelProperty(value = "执行任务类型")
    private String action;

    @ApiModelProperty(value = "当前用户", required = true)
    @NotBlank(message = "当前用户不能为空")
    private String userId;

    @ApiModelProperty(value = "签收、处理人Ids")
    private String assigneeIds;

    @ApiModelProperty("处理组Ids")
    private String assigneeGroups;

    @ApiModelProperty(value = "流程参数存储范围")
    private Boolean localScope = false;

    @ApiModelProperty(value = "批注")
    private String comment;

    @ApiModelProperty("参数键值对")
    private Map<String, Object> variables;
}
