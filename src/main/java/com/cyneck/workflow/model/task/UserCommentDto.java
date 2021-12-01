package com.cyneck.workflow.model.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>Description: 用户批注信息</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/22 15:31
 **/
@ApiModel(description = "用户批注信息")
@Data
public class UserCommentDto {

    @ApiModelProperty(value = "类型",position = 1)
    protected String type;

    @ApiModelProperty(value = "用户id",position = 2)
    protected String userId;

    @ApiModelProperty(value = "时间",position = 3)
    protected Date time;

    @ApiModelProperty(value = "任务id",position = 4)
    protected String taskId;

    @ApiModelProperty(value = "流程实例id",position = 5)
    protected String processInstanceId;

    @ApiModelProperty(value = "操作动作",position = 6)
    protected String action;

    @ApiModelProperty(value = "内容",position = 7)
    protected String message;

}
