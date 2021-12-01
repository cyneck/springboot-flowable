package com.cyneck.workflow.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/21 19:26
 **/
@Data
@ApiModel(value = "FormModel", description = "表单模型")
public class FormModel implements Serializable {

    private static final long serialVersionUID = 1l;

    @ApiModelProperty(value = "表单key（唯一性）", position = 1, required = true)
    private String key;

    @ApiModelProperty(value = "表单名", position = 2, required = true)
    private String name;

    @ApiModelProperty(value = "版本号", position = 3)
    private Integer version = 0;

    @ApiModelProperty(value = "表单字段模型", position = 4, required = true)
    private List<FormDataField> fields;


}
