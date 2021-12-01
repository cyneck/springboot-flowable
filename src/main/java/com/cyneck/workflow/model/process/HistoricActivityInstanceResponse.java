package com.cyneck.workflow.model.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.flowable.engine.ProcessEngineConfiguration;

import java.util.Date;

/**
 * <p>Description: 历史操作记录</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/22 09:40
 **/
@ApiModel("历史活动记录")
@Data
public class HistoricActivityInstanceResponse {

    @ApiModelProperty(value = "流程实例id",position = 1)
    protected String processInstanceId;

    @ApiModelProperty(value = "流程定义id",position = 2)
    protected String processDefinitionId;

    @ApiModelProperty(value = "开始时间",position = 3)
    protected Date startTime;

    @ApiModelProperty(value = "结束时间",position = 4)
    protected Date endTime;

    @ApiModelProperty(value = "处理耗时",position = 5)
    protected Long durationInMillis;

    @ApiModelProperty(value = "事务顺序",position = 6)
    protected Integer transactionOrder;

    @ApiModelProperty(value = "活动id",position = 7)
    protected String activityId;

    @ApiModelProperty(value = "活动名称",position = 8)
    protected String activityName;

    @ApiModelProperty(value = "活动类型",position = 9)
    protected String activityType;

    @ApiModelProperty(value = "执行id",position = 10)
    protected String executionId;

    @ApiModelProperty(value = "处理人",position = 11)
    protected String assignee;

    @ApiModelProperty(value = "任务id",position = 12)
    protected String taskId;

    @ApiModelProperty(value = "流程实例id",position = 13)
    protected String calledProcessInstanceId;

    @ApiModelProperty(value = "流程实例id",position = 14)
    protected String tenantId = ProcessEngineConfiguration.NO_TENANT_ID;
}
