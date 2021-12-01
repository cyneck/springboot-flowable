package com.cyneck.workflow.model.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>Description: 用户任务</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/22 11:01
 **/
@ApiModel(description = "用户任务")
@Data
public class UserTaskResponse {
    @ApiModelProperty(value = "任务id", position = 1)
    private String taskId;

//    @ApiModelProperty(value = "任务定义id", position = 2)
//    private String taskDefinitionId;

    @ApiModelProperty(value = "任务名称", position = 3)
    private String name;

    @ApiModelProperty(value = "处理人", position = 4)
    private String assignee;

    @ApiModelProperty(value = "创建时间", position = 5)
    private Date createTime;

    @ApiModelProperty(value = "结束时间", position = 6)
    private Date endTime;

    @ApiModelProperty(value = "处理耗时（毫秒）", position = 7)
    private Long durationInMillis;

    @ApiModelProperty(value = "任务所有者", position = 8)
    private String owner;

    @ApiModelProperty(value = "执行id", position = 9)
    private String executionId;

    @ApiModelProperty(value = "业务key", position = 10)
    private String businessKey;

    @ApiModelProperty(value = "业务名称", position = 11)
    private String businessName;

    @ApiModelProperty(value = "流程实例id", position = 12)
    private String processInstanceId;

    @ApiModelProperty(value = "流程实例名称", position = 13)
    private String processInstanceName;

    @ApiModelProperty(value = "流程定义id", position = 14)
    private String processDefinitionId;

    @ApiModelProperty(value = "流程定义key", position = 15)
    private String processDefinitionKey;

    @ApiModelProperty(value = "流程定义名", position = 16)
    private String processDefinitionName;
}
