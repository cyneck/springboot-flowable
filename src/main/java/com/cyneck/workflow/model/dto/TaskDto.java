package com.cyneck.workflow.model.dto;

import lombok.Data;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;

import java.util.Date;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/22 21:49
 **/
@Data
public class TaskDto extends TaskEntityImpl {
    private String owner;
    private int assigneeUpdatedCount;
    private String originalAssignee;
    private String assignee;
    private DelegationState delegationState;
    private String parentTaskId;
    private String name;
    private String localizedName;
    private String description;
    private String localizedDescription;
    private int priority = 50;
    private Date createTime;
    private Date dueDate;
    private int suspensionState;
    private String category;
    private boolean isIdentityLinksInitialized;
    private String executionId;
    private String processInstanceId;
    private String processDefinitionId;
    private String taskDefinitionId;
    private String scopeId;
    private String subScopeId;
    private String scopeType;
    private String scopeDefinitionId;
    private String propagatedStageInstanceId;
    private String taskDefinitionKey;
    private String formKey;
    private boolean isCanceled;
    private boolean isCountEnabled;
    private int variableCount;
    private int identityLinkCount;
    private int subTaskCount;
    private Date claimTime;
    private String tenantId;
    private String eventName;
    private String eventHandlerId;
}
