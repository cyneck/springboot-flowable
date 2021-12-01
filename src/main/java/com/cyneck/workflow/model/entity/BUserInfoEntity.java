package com.cyneck.workflow.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 业务用户信息
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/18 11:32
 **/
@ApiModel(description = "业务用户信息")
@Data
public class BUserInfoEntity {

    @ApiModelProperty(value = "业务用户id", position = 1)
    private String userId;

    @ApiModelProperty(value = "用户名称", position = 2)
    private String userName;

    @ApiModelProperty(value = "用户真实名字", position = 3)
    private String realName;

    @ApiModelProperty(value = "电子邮件地址", position = 4)
    private String email;

    @ApiModelProperty(value = "角色id", position = 5)
    private String roleId;

    @ApiModelProperty(value = "角色名", position = 6)
    private String roleName;

    @ApiModelProperty(value = "公司id", position = 7)
    private String companyId;

    @ApiModelProperty(value = "公司名", position = 8)
    private String companyName;

    @ApiModelProperty(value = "公司所在城市", position = 9)
    private String companyCity;

    @ApiModelProperty(value = "公司类型", position = 10)
    private String companyType;

}
