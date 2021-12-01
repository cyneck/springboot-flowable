package com.cyneck.workflow.model.dto;

import lombok.Data;
import org.flowable.engine.history.HistoricProcessInstance;

import java.util.Date;

/**
 * <p>Description: 历史流程实例响应</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/2 19:52
 **/
@Data
public class HistoricProcessInstanceDto {
    protected String id;
    private String businessKey;
    protected String processDefinitionId;
    private String processDefinitionName;
    private String processDefinitionKey;
    private Integer processDefinitionVersion;
    private Date startTime;
    private Date endTime;
    private Long durationInMillis;
    private String startUserId;
    private String startActivityId;
    private String superProcessInstanceId;
    protected String tenantId;

    public HistoricProcessInstanceDto change2Dto(HistoricProcessInstance processInstance) {
        HistoricProcessInstanceDto result = new HistoricProcessInstanceDto();
        result.setId(processInstance.getId());
        result.setBusinessKey(processInstance.getBusinessKey());
        result.setStartTime(processInstance.getStartTime());
        result.setEndTime(processInstance.getEndTime());
        result.setDurationInMillis(processInstance.getDurationInMillis());
        result.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        result.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        result.setProcessDefinitionName(processInstance.getProcessDefinitionName());
        result.setProcessDefinitionVersion(processInstance.getProcessDefinitionVersion());
        result.setStartActivityId(processInstance.getStartActivityId());
        result.setStartUserId(processInstance.getStartUserId());
        result.setSuperProcessInstanceId(processInstance.getSuperProcessInstanceId());
        result.setTenantId(processInstance.getTenantId());
        return result;
    }
}
