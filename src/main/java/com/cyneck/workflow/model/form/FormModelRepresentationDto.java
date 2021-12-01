package com.cyneck.workflow.model.form;

import com.cyneck.workflow.model.dto.ModelRepresentation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/21 18:39
 **/
@Data
@ApiModel(value = "FormModelRepresentationDto", description = "自定义表单模型表示")
public class FormModelRepresentationDto extends ModelRepresentation {

    @ApiModelProperty(value = "表单字段模型", position = 14, required = true)
    protected FormModel formModel;

    @ApiModelProperty(value = "表单模型类型，默认2", position = 15)
    protected Integer modelType = 2;

}
