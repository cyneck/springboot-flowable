package com.cyneck.workflow.model.task;

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
public class TaskRequestQuery {

    @ApiModelProperty(value = "任务ID", position = 1)
    private String taskId;

    @ApiModelProperty(value = "流程实例ID", position = 2)
    private String processInstanceId;

    @ApiModelProperty(value = "流程定义的Key", position = 3)
    private String processDefinitionKey;

//    @ApiModelProperty(value = "用户groupIds", position = 5,notes = "待办分组ID，逗号分隔，例：[\"ROLE_ADMIN\", \"ROLE_GROUP_LEADER\"]")
//    private String groupIds;

    @ApiModelProperty(value = "当前节点name", position = 6)
    private String name;

//    @ApiModelProperty(value = "流程实例key", position = 7)
//    private String taskDefinitionKey;

    @ApiModelProperty(value = "业务主键", position = 8)
    private String businessKey;

    @ApiModelProperty(value = "业务类型", position = 9)
    private String businessType;

    @ApiModelProperty(value = "业务名称", position = 10)
    private String businessName;

    @ApiModelProperty(value = "用户id", position = 10)
    private String userId;

    @ApiModelProperty(value = "页码", position = 11)
    private int size = 10;

    @ApiModelProperty(value = "当前页", position = 12)
    private int current = 1;
}
