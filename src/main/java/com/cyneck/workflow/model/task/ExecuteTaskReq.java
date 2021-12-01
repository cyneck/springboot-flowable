package com.cyneck.workflow.model.task;

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

    @ApiModelProperty(value = "任务执行类型枚举 (claim：签收; unclaim：反签收; complete:完成;" +
            " delegate:任务委派; resolve: 签收任务完成; assignee:任务转办; sign_backward：向后加签; " +
            "sign_forward: 向前加签; track:跟踪; carbon_copy:抄送; read:已阅; take_back:撤回)",required = true)
    private String action;

    @ApiModelProperty(value = "签收、处理人Id")
    private String assigneeId;

    @ApiModelProperty(value = "抄送用户Id（逗号分隔，字符串）")
    private String ccToUserIds;

//    @ApiModelProperty("处理组Ids")
//    private String assigneeGroup;

    @ApiModelProperty(value = "流程参数存储范围")
    private Boolean localScope = false;

    @ApiModelProperty(value = "批注")
    private String comment;

    @ApiModelProperty("补充表单键值对")
    private Map<String, Object> variables;
}
