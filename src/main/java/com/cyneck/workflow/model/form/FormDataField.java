package com.cyneck.workflow.model.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 表单字段模型
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/24 14:00
 **/
@Data
@ApiModel(value = "FormDataField", description = "表单字段模型")
public class FormDataField implements Serializable {

    private static final long serialVersionUID = 1l;

    @ApiModelProperty(value = "字段变量", position = 1, required = true)
    private String id;

    @ApiModelProperty(value = "显示名称", position = 2, required = true)
    private String name;

//    @ApiModelProperty("fieldType。默认：FormField")
//    @JsonIgnore
//    private String fieldType = "FormField";

    @ApiModelProperty(value = "表单字段类型,text", position = 3)
    private String type;

    @ApiModelProperty(value = "字段默认值", position = 4)
    private String value;

    @ApiModelProperty(value = "是否必须", position = 5)
    private Boolean required;

    @ApiModelProperty(value = "是否只读", position = 6)
    private Boolean readOnly;

    @ApiModelProperty(value = "是否重写id", position = 6)
    private Boolean overrideId;

//    @ApiModelProperty(value = "布局", position = 6)
//    @JsonIgnore
//    private String layout;

    @ApiModelProperty(value = "占位提示符", position = 7)
    private String placeholder;

}
