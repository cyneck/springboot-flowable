package com.cyneck.workflow.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 抄送用户
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/3 15:12
 **/
@Data
@ApiModel(description = "抄送用户")
public class CcToVo {

    @ApiModelProperty(value = "用户id",required = true)
    private String userId;

    @ApiModelProperty(value = "用户名",required = true)
    private String userName;

    @Override
    public String toString(){
        return String.format("%s[%s]",this.userName,this.userId);
    }
}

