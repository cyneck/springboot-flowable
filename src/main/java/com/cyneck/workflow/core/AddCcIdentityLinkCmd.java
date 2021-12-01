package com.cyneck.workflow.core;

import com.cyneck.workflow.common.ActionEnum;
import com.cyneck.workflow.common.FlowableConstant;
import com.cyneck.workflow.mapper.IdentitylinkMapper;
import com.cyneck.workflow.model.entity.ActRuIdentitylinkEntity;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.CommentEntity;
import org.flowable.engine.impl.persistence.entity.CommentEntityManager;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.identitylink.service.IdentityLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 抄送任务
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/3 15:10
 **/
public class AddCcIdentityLinkCmd implements Command<Void>, Serializable {

    protected String processInstanceId;
    protected String taskId;
    protected String userId;
    protected String[] ccIds;

    IdentitylinkMapper identitylinkMapper;

    /**
     * 抄送cmd
     *
     * @param processInstanceId 流程实例id
     * @param taskId            任务id
     * @param userId            当前用户id
     * @param ccToVos           抄送人，多个用户
     */
    public AddCcIdentityLinkCmd(String processInstanceId, String taskId, String userId, String[] ccToVos,IdentitylinkMapper identitylinkMapper) {
        validateParams(processInstanceId, taskId, userId, ccToVos);
        this.processInstanceId = processInstanceId;
        this.taskId = taskId;
        this.userId = userId;
        this.ccIds = ccToVos;
        this.identitylinkMapper=identitylinkMapper;
    }

    protected void validateParams(String processInstanceId, String taskId, String userId, String[] ccTo) {
        if (processInstanceId == null) {
            throw new FlowableIllegalArgumentException("processInstanceId is null");
        }
        if (taskId == null) {
            throw new FlowableIllegalArgumentException("taskId is null");
        }
        if (userId == null) {
            throw new FlowableIllegalArgumentException("userId is null");
        }
        if (ccTo == null || ccTo.length == 0) {
            throw new FlowableIllegalArgumentException("ccTo is null or empty");
        }
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        ExecutionEntity processInstance = executionEntityManager.findById(processInstanceId);
        if (processInstance == null) {
            throw new FlowableObjectNotFoundException("Cannot find process instance with id " + processInstanceId,
                    ExecutionEntity.class);
        }
        IdentityLinkService identityLinkService = CommandContextUtil.getIdentityLinkService(commandContext);
        for (String ccId : ccIds) {
            ActRuIdentitylinkEntity actRuIdentitylinkEntity = identitylinkMapper.getCCUserLinkByUserTaskId(ccId, taskId);
            if (actRuIdentitylinkEntity == null) {
                identityLinkService.createTaskIdentityLink(taskId, ccId, null, FlowableConstant.CC);
            }
//            IdentityLinkUtil.createProcessInstanceIdentityLink(processInstance, ccId, null, FlowableConstant.CC);
        }
        this.createCcComment(commandContext);
        return null;

    }

    protected void createCcComment(CommandContext commandContext) {
        CommentEntityManager commentEntityManager = CommandContextUtil.getCommentEntityManager(commandContext);
        CommentEntity comment = (CommentEntity) commentEntityManager.create();
        comment.setProcessInstanceId(processInstanceId);
        comment.setUserId(userId);
        comment.setType(FlowableConstant.CC); //抄送
        comment.setTime(CommandContextUtil.getProcessEngineConfiguration(commandContext).getClock().getCurrentTime());
        comment.setTaskId(taskId);
        comment.setAction(ActionEnum.CARBON_COPY.getAction());
        String ccToStr = StringUtils.arrayToCommaDelimitedString((Object[]) ccIds);
        comment.setMessage(ccToStr);
        comment.setFullMessage(ccToStr);
        commentEntityManager.insert(comment);
    }

}
