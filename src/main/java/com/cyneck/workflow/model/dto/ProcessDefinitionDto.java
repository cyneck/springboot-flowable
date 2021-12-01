package com.cyneck.workflow.model.dto;

import lombok.Data;
import org.flowable.engine.repository.ProcessDefinition;

/**
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/2 21:01
 **/
@Data
public class ProcessDefinitionDto {
    private String id;
    private String key;
    private int version;
    private String name;
    private String description;
    private String tenantId;
    private String category;
    private String formKey;
    private boolean graphicalNotationDefined = false;
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
