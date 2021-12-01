package com.cyneck.workflow.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 表单数据
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/20 14:32
 **/
@Data
@ApiModel(value = "FormDataDto", description = "表单数据")
public class FormDataDto {

    @ApiModelProperty(value = "任务Id", required = true, position = 1)
    private String taskId;

    @ApiModelProperty(value = "表单数据", position = 2)
    private Map<String, String> formData;
}
