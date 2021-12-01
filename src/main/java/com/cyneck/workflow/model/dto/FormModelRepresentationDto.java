package com.cyneck.workflow.model.dto;

import com.cyneck.workflow.common.ModelType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.flowable.ui.modeler.model.ModelRepresentation;

import java.util.Date;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/21 18:39
 **/
@Data
@ApiModel(value = "FormModelRepresentationDto", description = "新建表单模型")
public class FormModelRepresentationDto extends ModelRepresentation {

    @ApiModelProperty(value = "modelId", position = 1)
    protected String id;

    @ApiModelProperty(value = "模型名称", position = 2, required = true)
    protected String name;

    @ApiModelProperty(value = "模型key", position = 3, required = true)
    protected String key;

    @ApiModelProperty(value = "模型描述", position = 4)
    protected String description;

    @ApiModelProperty(value = "模型创建时间", position = 5)
    protected String createdBy;

    @ApiModelProperty(value = "模型更新人", position = 6)
    protected String lastUpdatedBy;

    @ApiModelProperty(value = "模型更新时间", position = 7)
    protected Date lastUpdated;

    @ApiModelProperty(value = "是否是最新版", position = 8)
    protected boolean latestVersion;

    @ApiModelProperty(value = "版本号", position = 9)
    protected int version = 1;

    @ApiModelProperty(value = "备注", position = 10)
    protected String comment;

    @ApiModelProperty(value = "表单字段模型", position = 11, required = true)
    protected FormModel formModel;

    /**
     * @see ModelType
     */
    @ApiModelProperty(value = "模型类型", position = 12)
    protected Integer modelType;

    @ApiModelProperty(value = "租户id", position = 13)
    protected String tenantId;
}
