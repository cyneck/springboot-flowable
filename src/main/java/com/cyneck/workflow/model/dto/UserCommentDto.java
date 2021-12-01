package com.cyneck.workflow.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/21 15:49
 **/
@Data
@ApiModel(value = "UserComment", description = "用户批注")
public class UserCommentDto {

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "批注")
    private String message;

    @ApiModelProperty(value = "用户id")
    private String userId;

}
