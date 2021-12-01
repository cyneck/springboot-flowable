package com.cyneck.workflow.model.process;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.history.HistoricProcessInstance;

import java.util.Date;
import java.util.Map;

/**
 * <p>Description: 流程实例dto</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/21 22:33
 **/
@ApiModel(description = "流程实例Dto")
@Data
@NoArgsConstructor
public class ProcessInstanceResponse {

    @ApiModelProperty(value = "流程实例id", position = 1, required = true)
    private String processInstanceId;

    @ApiModelProperty(value = "流程定义key", position = 2)
    private String processDefinitionKey;

    @ApiModelProperty(value = "流程定义id", position = 3)
    private String processDefinitionId;

    @ApiModelProperty(value = "流程定义name", position = 4)
    private String processDefinitionName;

    @ApiModelProperty(value = "流程定义版本号", position = 5)
    private Integer processDefinitionVersion;

    @ApiModelProperty(value = "流程部署id", position = 6)
    private String deploymentId;

    @ApiModelProperty(value = "业务key", position = 7)
    private String businessKey;

    @ApiModelProperty(value = "是否挂起", position = 8)
    private boolean isSuspended;

    @ApiModelProperty(value = "表单数据", position = 9)
    private Map<String, Object> processVariables;

    @ApiModelProperty(value = "租户id", position = 10)
    private String tenantId;

    @ApiModelProperty(value = "流程实例名", position = 11)
    private String name;

    @ApiModelProperty(value = "描述", position = 12)
    private String description;

    @ApiModelProperty(value = "流程实例的本地化名", position = 13)
    @JsonIgnore
    private String localizedName;

    @ApiModelProperty(value = "流程实例的本地描述", position = 14)
    @JsonIgnore
    private String localizedDescription;

    @ApiModelProperty(value = "开始时间", position = 15)
    private Date startTime;

    @ApiModelProperty(value = "发起用户id", position = 16)
    private String startUserId;

    @ApiModelProperty(value = "反馈id", position = 17)
    @JsonIgnore
    private String callbackId;

    @ApiModelProperty(value = "反馈类型", position = 18)
    @JsonIgnore
    private String callbackType;


    public ProcessInstanceResponse(HistoricProcessInstance historicProcessInstance) {
        this.processInstanceId = historicProcessInstance.getId();
        this.processDefinitionKey = historicProcessInstance.getProcessDefinitionKey();
        this.processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        this.processDefinitionName = historicProcessInstance.getProcessDefinitionName();
        this.processDefinitionVersion = historicProcessInstance.getProcessDefinitionVersion();
        this.deploymentId = historicProcessInstance.getDeploymentId();
        this.businessKey = historicProcessInstance.getBusinessKey();
        this.processVariables = historicProcessInstance.getProcessVariables();
        this.tenantId = historicProcessInstance.getTenantId();
        this.name = historicProcessInstance.getName();
        this.description = historicProcessInstance.getDescription();
        this.startTime = historicProcessInstance.getStartTime();
        this.startUserId = historicProcessInstance.getStartUserId();
        this.callbackId = historicProcessInstance.getCallbackId();
        this.callbackType = historicProcessInstance.getCallbackType();
    }
}
