package com.cyneck.workflow.model.dto;

import com.cyneck.workflow.common.ModelType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.flowable.ui.modeler.domain.AbstractModel;

import java.util.Date;

/**
 * 模型表示
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/20 13:46
 **/
@Data
@ApiModel(value = "ModelRepresentation", description = "模型表示")
public class ModelRepresentation extends AbstractModel {

    @ApiModelProperty(value = "modelId", position = 1)
    protected String id;

    @ApiModelProperty(value = "模型名称", position = 2, required = true)
    protected String name;

    @ApiModelProperty(value = "模型key", position = 3, required = true)
    protected String key;

    @ApiModelProperty(value = "模型描述", position = 4)
    protected String description;

    @ApiModelProperty(value = "模型创建时间", position = 5)
    protected Date created;

    @ApiModelProperty(value = "模型创建人", position = 6)
    protected String createdBy;

    @ApiModelProperty(value = "模型更新人", position = 7)
    protected String lastUpdatedBy;

    @ApiModelProperty(value = "模型更新时间", position = 8)
    protected Date lastUpdated;

    @ApiModelProperty(value = "是否是最新版", position = 9)
    protected boolean latestVersion;

    @ApiModelProperty(value = "版本号", position = 10)
    protected int version = 1;

    @ApiModelProperty(value = "备注", position = 11)
    protected String comment;
    /**
     * @see ModelType
     */
    @ApiModelProperty(value = "模型类型（0:流程，2:表单，3:应用）", position = 12)
    protected Integer modelType;

    @ApiModelProperty(value = "租户id", position = 13)
    protected String tenantId;

    @ApiModelProperty(value = "模型编辑器json", position = 14)
    @JsonIgnore
    protected String modelEditorJson;
}
