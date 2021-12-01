package com.cyneck.workflow.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/1 19:09
 **/
@Data
public class TaskEditRequest {
    private String id;
    private String name;
    private String assignee;
    private String owner;
    private Date dueDate;
    private String category;
    private String description;
    private int priority;
}
