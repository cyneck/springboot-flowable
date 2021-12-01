package com.cyneck.workflow.core;

import com.cyneck.workflow.common.ActionEnum;
import com.cyneck.workflow.common.FlowableConstant;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.cmd.AddCommentCmd;
import org.flowable.engine.impl.cmd.NeedsActiveTaskCmd;
import org.flowable.engine.impl.util.TaskHelper;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

/**
 * 已阅任务
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/3 14:08
 **/
public class CompleteTaskReadCmd extends NeedsActiveTaskCmd<Void> {


    private String userId;

    public CompleteTaskReadCmd(String taskId, String userId) {
        super(taskId);
        this.userId = userId;
    }

    @Override
    protected Void execute(CommandContext commandContext, TaskEntity task) {
        if (userId == null || userId.length() == 0) {
            throw new FlowableIllegalArgumentException("userId is null or empty");
        }
        if (!FlowableConstant.CATEGORY_TO_READ.equals(task.getCategory())) {
            throw new FlowableException("task category must be 'toRead'");
        }
        if (!userId.equals(task.getOwner()) && !userId.equals(task.getAssignee())) {
            throw new FlowableException("User does not have permission");
        }
        commandContext.getCommandExecutor().execute(new AddCommentCmd(taskId, task.getProcessInstanceId(),
                ActionEnum.READ.toString(), "已阅！"));
        TaskHelper.completeTask(task, null, null, null, null, commandContext);
        return null;
    }
}
