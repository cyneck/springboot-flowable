package com.cyneck.workflow.model.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.flowable.bpmn.model.CustomProperty;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.FormProperty;

import java.util.*;

/**
 * <p>Description: 流程进度节点list</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/21 23:45
 **/
@Data
@ApiModel(description = "流程实例的节点信息")
public class ProcessNodeResponse {

    @ApiModelProperty(value = "节点id", position = 1)
    private String activityId;

    @ApiModelProperty(value = "节点名字", position = 2)
    private String name;

    @ApiModelProperty(value = "任务执行id", position = 3)
    private String executionId;

    @ApiModelProperty(value = "处理人", position = 4)
    private String assignee;

    @ApiModelProperty(value = "截止日期", position = 5)
    private String dueDate;

    @ApiModelProperty(value = "节点任务所有者", position = 6)
    private String owner;

    @ApiModelProperty(value = "分类", position = 7)
    protected String category;

}
