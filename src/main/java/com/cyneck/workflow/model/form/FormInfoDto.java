package com.cyneck.workflow.model.form;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.flowable.form.api.FormModel;

/**
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/20 17:09
 **/
@ApiOperation(value = "表单信息")
@Data
public class FormInfoDto {
    @ApiModelProperty(value = "任务Id", position = 1)
    protected String id;

    @ApiModelProperty(value = "任务名", position = 2)
    protected String name;

    @ApiModelProperty(value = "任务描述", position = 3)
    protected String description;

    @ApiModelProperty(value = "任务key", position = 4)
    protected String key;

    @ApiModelProperty(value = "版本号", position = 5)
    protected int version;

}
