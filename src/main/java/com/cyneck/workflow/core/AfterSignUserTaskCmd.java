package com.cyneck.workflow.core;

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.cmd.AbstractDynamicInjectionCmd;
import org.flowable.engine.impl.dynamic.BaseDynamicSubProcessInjectUtil;
import org.flowable.engine.impl.dynamic.DynamicUserTaskBuilder;
import org.flowable.engine.impl.persistence.entity.*;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>Description: 向后加签</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:06
 **/
@Slf4j
public class AfterSignUserTaskCmd extends AbstractDynamicInjectionCmd implements Command<Void> {
    //流程实例id
    protected String processInstanceId;

    //后加签的节点信息
    private DynamicUserTaskBuilder dynamicUserTaskBuilder;

    private FlowElement currentFlowElement;

    private UserTask nextUserTask;

    private static final String IN_PREFIX = "in-";
    private static final String OUT_PREFIX = "out-";

    //当前流程节点的id
    private String taskId;

    public AfterSignUserTaskCmd(String processInstanceId, DynamicUserTaskBuilder signUserTaskBuilder, String taskId, UserTask nextUserTask) {
        this.processInstanceId = processInstanceId;
        this.dynamicUserTaskBuilder = signUserTaskBuilder;
        this.taskId = taskId;
        this.nextUserTask = nextUserTask;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        //AbstractDynamicInjectionCmd提供的修改方法入口
        //找到当前节点的实体
        TaskService taskService = CommandContextUtil.getTaskService(commandContext);
        TaskEntity taskEntity = taskService.getTask(taskId);
        if (taskEntity == null) {
            throw new FlowableObjectNotFoundException("task:" + taskId + " not found");
        }
        //查找当前节点对应的执行执行实体（感觉兴趣的可以搜下流程执行的实例信息 表为ACT_RU_EXECUTION）
        ExecutionEntity currentExecutionEntity = CommandContextUtil.getExecutionEntityManager(commandContext)
                .findById(taskEntity.getExecutionId());
        if (currentExecutionEntity == null) {
            throw new FlowableObjectNotFoundException("task:" + taskId + ",execution:"
                    + taskEntity.getExecutionId() + " not found");
        }
        currentFlowElement = currentExecutionEntity.getCurrentFlowElement();
        createDerivedProcessDefinitionForProcessInstance(commandContext, processInstanceId);
        return null;
    }


    //这个方法也可以单独写在一个工具类里
    public SequenceFlow createSequenceFlow(String from, String to) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        flow.setId("seq_" + UUID.randomUUID().toString());
        return flow;
    }

    @Override
    protected void updateBpmnProcess(CommandContext commandContext,
                                     org.flowable.bpmn.model.Process process,
                                     BpmnModel bpmnModel,
                                     ProcessDefinitionEntity originalProcessDefinitionEntity,
                                     DeploymentEntity newDeploymentEntity) {
        //找到当前节点的实体
        TaskService taskService = CommandContextUtil.getTaskService(commandContext);
        TaskEntity taskEntity = taskService.getTask(taskId);
        if (taskEntity == null) {
            throw new FlowableObjectNotFoundException("task:" + taskId + " not found");
        }
        //查找当前节点对应的执行执行实体（感觉兴趣的可以搜下流程执行的实例信息 表为ACT_RU_EXECUTION）
        ExecutionEntity currentExecutionEntity = CommandContextUtil.getExecutionEntityManager(commandContext).findById(taskEntity.getExecutionId());
        if (currentExecutionEntity == null) {
            throw new FlowableObjectNotFoundException("task:" + taskId + ",execution:" + taskEntity.getExecutionId() + " not found");
        }
        //当前节点id
        String activityId = currentExecutionEntity.getActivityId();
        FlowElement flowElement = process.getFlowElement(activityId, true);
        if (!(flowElement instanceof Task)) {
            throw new FlowableException("task type error");
        }

        if (currentFlowElement != null) {
            //定义新的节点
            UserTask newUserTask = new UserTask();
            newUserTask.setId(dynamicUserTaskBuilder.getId()!=null?dynamicUserTaskBuilder.getId():dynamicUserTaskBuilder.nextTaskId(process.getFlowElementMap()));
            dynamicUserTaskBuilder.setDynamicTaskId(UUID.randomUUID().toString());
            newUserTask.setName(dynamicUserTaskBuilder.getName());
            newUserTask.setAssignee(dynamicUserTaskBuilder.getAssignee());
            //当前节点为，一个流入，两个流出
            UserTask currentFlowElement = (UserTask) this.currentFlowElement;

            List<SequenceFlow> currentUserTaskOutList = new ArrayList<>();
            List<SequenceFlow> currentUserTaskInList = new ArrayList<>();

            List<SequenceFlow> newUserTaskInFlows = new ArrayList<>();
            List<SequenceFlow> newUserTaskOutFlows = new ArrayList<>();

            /* 重新调整边的连接关系*/

            //遍历当前节点的输出
            for (SequenceFlow sequenceFlowTmp : currentFlowElement.getOutgoingFlows()) {
                Boolean isNext = nextUserTask.getId().equals(sequenceFlowTmp.getTargetRef());
                if (isNext) {
                    // 新节点流出
                    // 新边的流出指向下一个
                    SequenceFlow newFlowOut = createSequenceFlow(newUserTask.getId(), sequenceFlowTmp.getTargetRef());
                    newFlowOut.setConditionExpression(sequenceFlowTmp.getConditionExpression());
                    newFlowOut.setName(sequenceFlowTmp.getName());

                    newUserTaskOutFlows.add(newFlowOut);

                    //记录历史变更中
                    recordNewSequence(commandContext, newFlowOut, currentFlowElement.getExtensionId(),
                            originalProcessDefinitionEntity.getId());

                    // 新加入process中边元素
                    process.addFlowElement(newFlowOut);

                    // 新节点的流入
                    // 当前节点指向新建节点
                    SequenceFlow currentFlowOut = createSequenceFlow(sequenceFlowTmp.getSourceRef(), newUserTask.getId());
                    currentFlowOut.setConditionExpression(sequenceFlowTmp.getConditionExpression());
                    currentFlowOut.setName(sequenceFlowTmp.getName());
                    // 新节点的流入
                    newUserTaskInFlows.add(currentFlowOut);

                    // 当前节点的流出方向
                    currentUserTaskOutList.add(currentFlowOut);

                    //新加入process中边元素
                    process.addFlowElement(currentFlowOut);

                    //删除原先节点的入线
                    process.removeFlowElement(sequenceFlowTmp.getId());

                    //记录历史变更中
                    recordNewSequence(commandContext, currentFlowOut, currentFlowElement.getExtensionId(),
                            originalProcessDefinitionEntity.getId());
                }
            }

            // 当前节点流入
            for (SequenceFlow sequenceFlow : currentFlowElement.getIncomingFlows()) {
                Boolean isNext = nextUserTask.getId().equals(sequenceFlow.getSourceRef());
                if (isNext) {
                    // 下一个到当前的改为
                    // 下一个到新节点
                    SequenceFlow newSequenceFlowIn = createSequenceFlow(sequenceFlow.getSourceRef(), newUserTask.getId());
                    newSequenceFlowIn.setConditionExpression(sequenceFlow.getConditionExpression());
                    newSequenceFlowIn.setName(sequenceFlow.getName());
                    // 新建节点的流入
                    newUserTaskInFlows.add(newSequenceFlowIn);
                    //新加入process中边元素
                    process.addFlowElement(newSequenceFlowIn);
                    //删除原先节点的入线
                    process.removeFlowElement(sequenceFlow.getId());

                    // 新节点到当前
                    SequenceFlow newSequenceFlow = createSequenceFlow(newUserTask.getId(), sequenceFlow.getTargetRef());
                    newSequenceFlow.setConditionExpression(sequenceFlow.getConditionExpression());
                    newSequenceFlow.setName(sequenceFlow.getName());
                    // 新建节点的流出
                    newUserTaskOutFlows.add(newSequenceFlow);
                    //当前节点的流入
                    currentUserTaskInList.add(newSequenceFlow);
                    //新加入process中边元素
                    process.addFlowElement(newSequenceFlow);
                    //记录历史变更中
                    recordNewSequence(commandContext, newSequenceFlowIn, currentFlowElement.getExtensionId(),
                            originalProcessDefinitionEntity.getId());

                } else {
                    // 上一个节点到当前
                    currentUserTaskInList.add(sequenceFlow);
                }

            }
            //新节点的流入
            newUserTask.setIncomingFlows(newUserTaskInFlows);
            // 新节点的流出
            newUserTask.setOutgoingFlows(newUserTaskOutFlows);
            //当前节点的流入
            currentFlowElement.setIncomingFlows(currentUserTaskInList);
            // 当前节点的流出
            currentFlowElement.setOutgoingFlows(currentUserTaskOutList);

            //加入新节点元素
            process.addFlowElement(newUserTask);

            /* 生成BPMN自动布局 */
            new BpmnAutoLayout(bpmnModel).execute();
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
            String processXml = new String(bpmnBytes);
            log.info("向后加签模型:\n{}", processXml);
            //当前流程的重新定义
            BaseDynamicSubProcessInjectUtil.processFlowElements(commandContext, process, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity);
        }

    }

    @Override
    protected void updateExecutions(CommandContext commandContext,
                                    ProcessDefinitionEntity processDefinitionEntity,
                                    ExecutionEntity executionEntity,
                                    List<ExecutionEntity> childExecutions) {
        //不更新execution，活动节点仍然是当前

    }

    private void recordNewSequence(CommandContext commandContext,
                                   SequenceFlow sequenceFlow,
                                   String extensionId,
                                   String procDefId) {
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        List<ExecutionEntity> oldExecution = executionEntityManager.findChildExecutionsByProcessInstanceId(processInstanceId);

        HistoricActivityInstanceEntityManager historicActivityInstanceEntityManager = CommandContextUtil.getHistoricActivityInstanceEntityManager(commandContext);
        HistoricActivityInstanceEntity historicActivityInstanceEntity = historicActivityInstanceEntityManager.create();
        historicActivityInstanceEntity.setActivityName(sequenceFlow.getName());
        historicActivityInstanceEntity.setActivityId(sequenceFlow.getId());
        historicActivityInstanceEntity.setExecutionId(oldExecution.get(0).getId());
        historicActivityInstanceEntity.setTenantId(oldExecution.get(0).getTenantId());
        historicActivityInstanceEntity.setProcessInstanceId(processInstanceId);
        historicActivityInstanceEntity.setActivityType("sequenceFlow");
        historicActivityInstanceEntity.setProcessDefinitionId(procDefId);
        historicActivityInstanceEntity.setStartTime(new Date());
        historicActivityInstanceEntity.setEndTime(new Date());

        historicActivityInstanceEntityManager.insert(historicActivityInstanceEntity);

        System.out.println("historicActivityInstanceEntity = " + historicActivityInstanceEntity);
    }


    /**
     * 检验模型
     *
     * @param bpmnModel
     */
    private void checkBpmnModel(BpmnModel bpmnModel) {
        //校验bpmModel
        ProcessValidator processValidator = new ProcessValidatorFactory().createDefaultProcessValidator();
        List<ValidationError> validationErrorList = processValidator.validate(bpmnModel);
        if (validationErrorList.size() > 0) {
            throw new RuntimeException("流程有误，请检查后重试");
        }

    }

}
