package com.cyneck.workflow.model.entity;

import com.cyneck.workflow.model.form.FormModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表单模型entity
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/24 11:05
 **/
@Data
@ApiModel(value = "ActDeModelEntity", description = "模型定义实体")
public class FormDeModelEntity {

    private static final long serialVersionUID = 4977531537706165288L;

    @ApiModelProperty(value = "模型id", position = 1)
    private String id;

    @ApiModelProperty(value = "模型名", position = 2)
    private String name;

    @ApiModelProperty(value = "模型类型（0:流程，2:表单，3:应用）", position = 3)
    private String modelKey;

    @ApiModelProperty(value = "描述", position = 4)
    private String description;

    @ApiModelProperty(value = "备注", position = 5)
    private String modelComment;

    @ApiModelProperty(value = "创建时间", position = 6)
    private LocalDateTime created;

    @ApiModelProperty(value = "创建人", position = 7)
    private String createdBy;

    @ApiModelProperty(value = "修改时间", position = 8)
    private LocalDateTime lastUpdated;

    @ApiModelProperty(value = "修改人", position = 9)
    private String lastUpdatedBy;

    @ApiModelProperty(value = "版本", position = 10)
    private Integer version;

    @ApiModelProperty(value = "模型编辑器需要的json字符串", position = 11)
    private FormModel formModel ;

    @ApiModelProperty(value = "租户id", position = 13)
    private String tenantId;

    @ApiModelProperty(value = "关联的子模型", position = 14)
    public List<ActDeModelEntity> children;
}
