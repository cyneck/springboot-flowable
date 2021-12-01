package com.cyneck.workflow.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * <p>Description: 历史任务</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/1 17:07
 **/
@Data
public class HistoricTaskDto {
    protected String id;
    protected String processDefinitionId;
    protected String processInstanceId;
    protected String name;
    protected String description;
    protected String owner;
    protected String assignee;
    protected Date startTime;
    protected Date endTime;
    protected Long durationInMillis;
    protected Long workTimeInMillis;
    protected Date claimTime;
    protected String taskDefinitionKey;
    protected String formKey;
    protected Integer priority;
    protected Date dueDate;
    protected String parentTaskId;
    protected String tenantId;
    protected String category;
}
