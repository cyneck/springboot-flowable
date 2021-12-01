package com.cyneck.workflow.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 任务执行，表单已提交对象
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/20 14:45
 **/
@ApiModel(value = "FormDataDto", description = "表单数据")
@Data
public class TaskCompleteFormData {

    @ApiModelProperty(value = "任务Id", position = 1)
    private String taskId;

    @ApiModelProperty(value = "填写完成任务的表单定义的ID", position = 2)
    private String formDefinitionId;

    @ApiModelProperty(value = "表单数据variables", position = 3)
    private Map<String, Object> formData;
}
