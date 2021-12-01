package com.cyneck.workflow.model.task;

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
@ApiModel(value = "UserCommentRep", description = "用户批注请求")
public class UserCommentRep {

    @ApiModelProperty(value = "任务id", required = true, position = 0)
    private String taskId;

    @ApiModelProperty(value = "批注", required = true, position = 1)
    private String message;

    @ApiModelProperty(value = "用户id", required = true, position = 2)
    private String userId;

    @ApiModelProperty(value = "批注类型", required = false, position = 3)
    private String type;

}
