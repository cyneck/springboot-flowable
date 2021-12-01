package com.cyneck.workflow.model.form;

import com.cyneck.workflow.model.task.TaskResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 表单详情数据
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/20 15:17
 **/
@ApiModel(value = "FormDataDetailDto", description = "任务表单数据")
@Data
public class FormDataDetailDto {

    @ApiModelProperty(value = "任务对象", position = 0)
    private TaskResponse task;

    @ApiModelProperty(value = "表单数据", position = 1)
    private Map<String, String> formData;

    /**
     * 与该表单相关的流程定义的部署标识id
     */
    @ApiModelProperty(value = "与该表单相关的流程定义的部署标识id", position = 2)
    private String deploymentId;
}
