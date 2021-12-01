package com.cyneck.workflow.model.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/1 19:09
 **/
@Data
@ApiModel(value = "TaskEditRequest", description = "任务编辑响应")
public class TaskEditRequest {

    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "name", position = 2)
    private String name;

    @ApiModelProperty(value = "处理人", position = 3)
    private String assignee;

    @ApiModelProperty(value = "任务所有者", position = 4)
    private String owner;

    @ApiModelProperty(value = "截止日", position = 5)
    private Date dueDate;

    @ApiModelProperty(value = "分类", position = 6)
    private String category;

    @ApiModelProperty(value = "描述", position = 7)
    private String description;

    @ApiModelProperty(value = "优先级", position = 8)
    private int priority;
}
