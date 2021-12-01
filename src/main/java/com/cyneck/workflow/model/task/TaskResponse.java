package com.cyneck.workflow.model.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 任务响应
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/2 20:05
 **/
@Data
@ApiModel(value = "TaskResponse", description = "任务响应对象")
public class TaskResponse {
    @ApiModelProperty(value = "任务id", position = 1)
    protected String id;

    @ApiModelProperty(value = "任务名", position = 2)
    protected String name;

    @ApiModelProperty(value = "流程定义id", position = 3)
    protected String processDefinitionId;

    @ApiModelProperty(value = "流程定义名", position = 4)
    protected String processDefinitionName;

    @ApiModelProperty(value = "流程定义key", position = 5)
    protected String processDefinitionKey;

    @ApiModelProperty(value = "流程定义版本", position = 6)
    protected Integer processDefinitionVersion;

    @ApiModelProperty(value = "流程实例id", position = 7)
    protected String processInstanceId;

    @ApiModelProperty(value = "任务处理人", position = 8)
    protected String assignee;

    @ApiModelProperty(value = "任务发起人", position = 9)
    protected String owner;

    @ApiModelProperty(value = "委托状态（PENDING：挂起， RESOLVED：已完成委托）", position = 10)
    protected String delegationState;

    @ApiModelProperty(value = "挂起状态", position = 11)
    protected boolean suspended;

    @ApiModelProperty(value = "描述", position = 12)
    protected String description;

    @ApiModelProperty(value = "分类", position = 13)
    protected String category;

    @ApiModelProperty(value = "创建时间", position = 14)
    protected Date createTime;

    @ApiModelProperty(value = "耗时", position = 15)
    protected Date dueDate;

    @ApiModelProperty(value = "优先级", position = 16)
    protected Integer priority;

    @ApiModelProperty(value = "任务定义key", position = 17)
    protected String taskDefinitionKey;

    @ApiModelProperty(value = "父级任务", position = 18)
    protected String parentTaskId;

    @ApiModelProperty(value = "表单key", position = 19)
    protected String formKey;

    @ApiModelProperty(value = "租户", position = 20)
    protected String tenantId;

}
