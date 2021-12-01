package com.cyneck.workflow.model.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.flowable.engine.repository.ProcessDefinition;

/**
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/2 21:01
 **/
@Data
@ApiModel(value = "ProcessDefinitionDto", description = "流程定义Dto")
public class ProcessDefinitionDto {

    @ApiModelProperty(value = "流程定义id", position = 1,required = true)
    private String id;

    @ApiModelProperty(value = "流程定义key", position = 2,required = true)
    private String key;

    @ApiModelProperty(value = "版本号", position = 3,required = true)
    private int version;

    @ApiModelProperty(value = "流程名称", position = 4,required = true)
    private String name;

    @ApiModelProperty(value = "描述", position = 5)
    private String description;

    @ApiModelProperty(value = "租户Id", position = 6)
    private String tenantId;

    @ApiModelProperty(value = "分类", position = 7)
    private String category;

    @ApiModelProperty(value = "表单key", position = 8)
    private String formKey;

    @ApiModelProperty(value = "是否是图形符号定义", position = 9)
    private boolean graphicalNotationDefined = false;

    @ApiModelProperty(value = "是否挂起", position = 10)
    private boolean suspended = false;

    public ProcessDefinitionDto createProcessDefinitionResponse(ProcessDefinition processDefinition, String formKey) {
        ProcessDefinitionDto response = new ProcessDefinitionDto();
        response.setId(processDefinition.getId());
        response.setKey(processDefinition.getKey());
        response.setVersion(processDefinition.getVersion());
        response.setCategory(processDefinition.getCategory());
        response.setName(processDefinition.getName());
        response.setDescription(processDefinition.getDescription());
        response.setSuspended(processDefinition.isSuspended());
        response.setGraphicalNotationDefined(processDefinition.hasGraphicalNotation());
        response.setTenantId(processDefinition.getTenantId());
        response.setFormKey(formKey);
        return response;
    }
}
