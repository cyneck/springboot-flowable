package com.cyneck.workflow.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公司信息
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/18 12:35
 **/
@ApiModel(description = "业务单位组信息")
@Data
public class BCompanyInfoEntity {

    @ApiModelProperty(value = "业务用户单位组id", position = 1)
    private String id;

    @ApiModelProperty(value = "别名", position = 1)
    private String alias;

    @ApiModelProperty(value = "用户单位组名称", position = 1)
    private String name;

    @ApiModelProperty(value = "单位组类型", position = 1)
    private String type;

    @ApiModelProperty(value = "单位组所属城市", position = 1)
    private String city;

    @ApiModelProperty(value = "角色范围", position = 1)
    private String roleScore;

    @ApiModelProperty(value = "角色名称", position = 1)
    private String roleName;
}
