package com.cyneck.workflow.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.flowable.ui.modeler.domain.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Description: 模型</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/16 14:53
 **/

@Slf4j
@Data
@ApiModel(value = "ModelDto", description = "模型Dto")
public class ModelDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模型id", position = 1)
    private String id;

    @ApiModelProperty(value = "模型名", position = 2)
    private String name;

    @ApiModelProperty(value = "模型key", position = 3)
    private String key;

    @ApiModelProperty(value = "描述", position = 4)
    private String description;

    @ApiModelProperty(value = "创建时间", position = 5)
    private Date created;

    @ApiModelProperty(value = "更新时间", position = 6)
    private Date lastUpdated;

    @ApiModelProperty(value = "创建人", position = 7)
    private String createdBy;

    @ApiModelProperty(value = "修改人", position = 8)
    private String lastUpdatedBy;

    @ApiModelProperty(value = "版本号", position = 9)
    private Integer version;

    @ApiModelProperty(value = "备注", position = 10)
    private String comment;

    @ApiModelProperty(value = "模型类型（0:流程，2:表单，3:应用）", position = 11)
    private Integer modelType;

    @ApiModelProperty(value = "租户id", position = 12)
    private String tenantId;

//    private Map<String, Object> aboutModel;

    public ModelDto() {
    }

    public ModelDto(Model model) {
        this.setId(model.getId());
        this.setName(model.getName());
        this.setKey(model.getKey());
        this.setDescription(model.getDescription());
        this.setCreated(model.getCreated());
        this.setLastUpdated(model.getLastUpdated());
        this.setCreatedBy(model.getCreatedBy());
        this.setLastUpdatedBy(model.getLastUpdatedBy());
        this.setVersion(model.getVersion());
        this.setComment(model.getComment());
        this.setModelType(model.getModelType());
        this.setTenantId(model.getTenantId());
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            this.setAboutModel(objectMapper.readValue(model.getModelEditorJson(), HashMap.class));
//        } catch (JsonProcessingException e) {
//            log.error("aboutModel field transform failure!", e);
//        }

    }
}
