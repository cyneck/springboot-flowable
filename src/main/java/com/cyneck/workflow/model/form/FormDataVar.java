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
 * @since 2021/2/20 18:06
 **/
@ApiModel(value = "FormData", description = "流程表单数据")
@Data
public class FormDataVar {

    @ApiModelProperty(value = "流程实例id",required = true,position = 1)
    private String processInstanceId ;

    @ApiModelProperty(value = "数据对象", position = 2)
    private Map<String,String> formData;

}
