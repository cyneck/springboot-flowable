package com.cyneck.workflow.model.process;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "HistoricProcessInstanceDto", description = "历史流程实例响应")
public class HistoricProcessInstanceResponse {

    @ApiModelProperty(value = "流程实例id", position = 1,required = true)
    protected String id;

    @ApiModelProperty(value = "流程定义id", position = 3,required = true)
    protected String processDefinitionId;

    @ApiModelProperty(value = "流程定义名", position = 4,required = true)
    private String processDefinitionName;

    @ApiModelProperty(value = "流程定义key", position = 5,required = true)
    private String processDefinitionKey;

    @ApiModelProperty(value = "流程定义版本号", position = 6,required = true)
    private Integer processDefinitionVersion;

    @ApiModelProperty(value = "业务key", position = 2,required = true)
    private String businessKey;

    @ApiModelProperty(value = "流程开始时间", position = 7,required = true)
    private Date startTime;

    @ApiModelProperty(value = "流程结束时间", position = 8,required = true)
    private Date endTime;

    @ApiModelProperty(value = "处理耗时", position = 9,required = true)
    private Long durationInMillis;

    @ApiModelProperty(value = "发起用户id", position = 10,required = true)
    private String startUserId;

    @ApiModelProperty(value = "发起任务id", position = 11,required = true)
    private String startActivityId;

    @ApiModelProperty(value = "父级流程实例id", position = 12,required = true)
    @JsonIgnore
    private String superProcessInstanceId;

    @ApiModelProperty(value = "租户id", position = 13,required = true)
    protected String tenantId;

    public HistoricProcessInstanceResponse change2Dto(HistoricProcessInstance processInstance) {
        HistoricProcessInstanceResponse result = new HistoricProcessInstanceResponse();
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
