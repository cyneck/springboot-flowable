package com.cyneck.workflow.model.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <p>Description: 历史任务</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/22 15:15
 **/
@ApiModel(description = "流程实例的历史任务")
@Data
public class HistoricAcitivityInstanceResponse {

//    @ApiModelProperty(value = "流程实例的活动任务id",position = 1)
//    private String id;

    @ApiModelProperty(value = "活动节点id",position = 2)
    private String activityId;

    @ApiModelProperty(value = "活动节点名称",position = 3)
    private String activityName;

    @ApiModelProperty(value = "活动类型",position = 4)
    private String activityType;

    @ApiModelProperty(value = "流程定义id",position = 5)
    private String processDefinitionId;

    @ApiModelProperty(value = "流程实例id",position = 6)
    private String processInstanceId;

    @ApiModelProperty(value = "执行id",position = 7)
    private String executionId;

    @ApiModelProperty(value = "任务id",position = 8)
    private String taskId;

    @ApiModelProperty(value = "处理人",position = 9)
    private  String assignee;

    @ApiModelProperty(value = "开始时间",position = 10)
    private Date startTime;

    @ApiModelProperty(value = "结束时间",position = 11)
    private Date endTime;

    @ApiModelProperty(value = "处理耗时",position = 12)
    private Long durationInMillis;

    @ApiModelProperty(value = "批注集合",position = 12)
    private List<UserCommentDto> comments;

//    @ApiModelProperty(value = "活动节点删除原因",position = 13)
//    private String DeleteReason;

    @ApiModelProperty(value = "租户id",position = 14)
    private String tenantId;

    @ApiModelProperty(value = "修订版本",position = 15)
    private String revision;
}
