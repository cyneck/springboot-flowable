package com.cyneck.workflow.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class FormModel {

    @ApiModelProperty(value = "表单key（唯一性）", position = 1, required = true)
    private String key;

    @ApiModelProperty(value = "表单名", position = 2, required = true)
    private String name;

    @ApiModelProperty(value = "版本号", position = 3)
    private String version = "1";

    @ApiModelProperty(value = "表单字段模型", position = 4, required = true)
    private List<FormDataField> fields;


    @Data
    @ApiModel(value = "FormDataField", description = "表单字段模型")
    class FormDataField {

        @ApiModelProperty(value = "字段变量", position = 1, required = true)
        private String id;

        @ApiModelProperty(value = "显示名称", position = 2, required = true)
        private String name;

        @ApiModelProperty("fieldType。默认：FormField")
        @JsonIgnore
        private String fieldType = "FormField";

        @ApiModelProperty(value = "表单字段类型,text", position = 3)
        private String type;

        @ApiModelProperty(value = "字段默认值", position = 4)
        private String value;

        @ApiModelProperty(value = "是否必须", position = 5)
        private Boolean required;

        @ApiModelProperty(value = "是否只读", position = 6)
        private Boolean readOnly;

        @ApiModelProperty(value = "占位提示符", position = 7)
        private String placeholder;
    }

}
