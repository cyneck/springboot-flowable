package com.cyneck.workflow.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:36
 **/
@ApiModel(value = "TaskRequestQuery", description = "任务查询条件对象req")
@Data
public class TaskReq {

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户groupIds")
    private List<String> groupIds;

    @ApiModelProperty("当前节点name")
    private String name;

    @ApiModelProperty("流程实例key")
    private String taskDefinitionKey;

    @ApiModelProperty("业务主键")
    private String businessKey;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("业务名称")
    private String businessName;

    @ApiModelProperty("流程实例ID")
    private String processInstanceId;

    @ApiModelProperty("流程定义的Key")
    private String processDefinitionKey;

    @ApiModelProperty("页码")
    private int size = 10;

    @ApiModelProperty("当前页")
    private int current = 1;
}
