package com.cyneck.workflow.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * 任务响应
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/2 20:05
 **/
@Data
public class TaskResponse {
    protected String id;
    protected String name;
    protected String assignee;
    protected String owner;
    protected String delegationState;
    protected boolean suspended;
    protected String description;
    protected String category;
    protected Date createTime;
    protected Date dueDate;
    protected Integer priority;
    protected String taskDefinitionKey;
    protected String parentTaskId;
    protected String formKey;
    protected String tenantId;
    protected String processDefinitionId;
    protected String processDefinitionName;
    protected String processDefinitionKey;
    protected Integer processDefinitionVersion;
    protected String processInstanceId;
}
