package com.cyneck.workflow.core;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.cmd.AbstractDynamicInjectionCmd;
import org.flowable.engine.impl.context.Context;
import org.flowable.engine.impl.dynamic.BaseDynamicSubProcessInjectUtil;
import org.flowable.engine.impl.dynamic.DynamicUserTaskBuilder;
import org.flowable.engine.impl.persistence.entity.*;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.flowable.bpmn.model.Process;

import java.util.*;

/**
 * <p>Description:向签加签</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:07
 **/
// fixme: 向前后，活动节点向前移动，但是历史记录中保留了痕迹（打印图流程图可知）
@Slf4j
public class BeforeSignUserTaskCmd extends AbstractDynamicInjectionCmd implements Command<Void> {
    //流程实例id
    protected String processInstanceId;

    //前加签的节点信息
    private DynamicUserTaskBuilder dynamicUserTaskBuilder;

    private FlowElement currentFlowElement;

    //当前流程节点的id
    private String taskId;

    private static final String IN_PREFIX = "in-";
    private static final String OUT_PREFIX = "out-";

    public BeforeSignUserTaskCmd(String processInstanceId, DynamicUserTaskBuilder signUserTaskBuilder, String taskId) {
        this.processInstanceId = processInstanceId;
        this.dynamicUserTaskBuilder = signUserTaskBuilder;
        this.taskId = taskId;
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

    @Override
    @Transactional
    protected void updateBpmnProcess(CommandContext commandContext,
                                     Process process,
                                     BpmnModel bpmnModel,
                                     ProcessDefinitionEntity originalProcessDefinitionEntity,
                                     DeploymentEntity newDeploymentEntity) {
        List<StartEvent> startEvents = process.findFlowElementsOfType(StartEvent.class);
        StartEvent initialStartEvent = null;
        for (StartEvent startEvent : startEvents) {
            if (startEvent.getEventDefinitions().size() == 0) {
                initialStartEvent = startEvent;
                break;

            } else if (initialStartEvent == null) {
                initialStartEvent = startEvent;
            }
        }
        if (currentFlowElement != null) {

            //定义新的节点
            UserTask newUserTask = new UserTask();
            BeanUtils.copyProperties(currentFlowElement, newUserTask);
            if (dynamicUserTaskBuilder.getId() != null) {
                newUserTask.setId(dynamicUserTaskBuilder.getId());
            } else {
                newUserTask.setId(dynamicUserTaskBuilder.nextTaskId(process.getFlowElementMap()));
            }
            dynamicUserTaskBuilder.setDynamicTaskId(UUID.randomUUID().toString());
            newUserTask.setId(dynamicUserTaskBuilder.getId());
            newUserTask.setName(dynamicUserTaskBuilder.getName());
            newUserTask.setAssignee(dynamicUserTaskBuilder.getAssignee());
            //当前节点为，一个流入，两个流出
            UserTask currentFlowElement = (UserTask) this.currentFlowElement;

            HashMap<String, String> nodeDic = new HashMap<>();

            List<SequenceFlow> currentUserTaskFlowOutList = new ArrayList<>();
            List<SequenceFlow> currentUserTaskFlowInList = new ArrayList<>();

            List<SequenceFlow> newTaskincomingFlows = new ArrayList<>();
            List<SequenceFlow> newTaskoutgoingFlows = new ArrayList<>();

            //新边，流入构建
            for (SequenceFlow sequenceFlowTmp : currentFlowElement.getIncomingFlows()) {
                //新节点的流入
                SequenceFlow sequenceFlowIn = new SequenceFlow();
                BeanUtil.copyProperties(sequenceFlowTmp, sequenceFlowIn);
                sequenceFlowIn.setSourceRef(sequenceFlowTmp.getSourceRef());
                sequenceFlowIn.setTargetRef(newUserTask.getId());
                sequenceFlowIn.setId("seq_" + UUID.randomUUID().toString());

                //新节点的流入
                newTaskincomingFlows.add(sequenceFlowIn);

                ////记录历史变更中
//                recordNewSequence(commandContext, sequenceFlowIn, currentFlowElement.getExtensionId(),
//                        originalProcessDefinitionEntity.getId());

                //当前节点的流入方向
                SequenceFlow currentUserTaskFlow = new SequenceFlow();
                BeanUtil.copyProperties(sequenceFlowTmp, currentUserTaskFlow);
                currentUserTaskFlow.setSourceRef(newUserTask.getId());
                currentUserTaskFlow.setId("seq_" + UUID.randomUUID().toString());
                currentUserTaskFlowInList.add(currentUserTaskFlow);

                //当前节点的流入，即为新建节点的流出
                newTaskoutgoingFlows.add(currentUserTaskFlow);

                //加入当前节点的流入
                process.addFlowElement(currentUserTaskFlow);

                //新加入process中边元素
                process.addFlowElement(sequenceFlowIn);

                //删除原先节点的入线
                process.removeFlowElement(sequenceFlowTmp.getId());

                //保存当前节点的上一个节点
                nodeDic.put(IN_PREFIX + sequenceFlowTmp.getSourceRef(), sequenceFlowTmp.getSourceRef());

            }


            for (SequenceFlow sequenceFlowTmp : currentFlowElement.getOutgoingFlows()) {
                boolean hasNode = nodeDic.containsKey(IN_PREFIX + sequenceFlowTmp.getTargetRef());
                if (hasNode) {
                    //复制当前节点，新边的流出调整
                    SequenceFlow newFlowOut = new SequenceFlow();
                    BeanUtil.copyProperties(sequenceFlowTmp, newFlowOut);
                    newFlowOut.setSourceRef(newUserTask.getId());
                    newFlowOut.setTargetRef(hasNode ? sequenceFlowTmp.getTargetRef() : sequenceFlowTmp.getSourceRef());
                    newFlowOut.setId("seq_" + UUID.randomUUID().toString());
//                    //记录历史变更中
//                    recordNewSequence(commandContext, newFlowOut, currentFlowElement.getExtensionId(),
//                            originalProcessDefinitionEntity.getId());
                    newTaskoutgoingFlows.add(newFlowOut);

                    //新加入process中边元素
                    process.addFlowElement(newFlowOut);

                    //重构当前节点的流出方向,当前节点指向新建节点
                    SequenceFlow currentUserTaskFlow = new SequenceFlow();
                    BeanUtil.copyProperties(sequenceFlowTmp, currentUserTaskFlow);
                    currentUserTaskFlow.setId("seq_" + UUID.randomUUID().toString());
                    currentUserTaskFlow.setTargetRef(hasNode ? newUserTask.getId() : currentFlowElement.getId());

                    currentUserTaskFlowOutList.add(currentUserTaskFlow);
                    newTaskincomingFlows.add(currentUserTaskFlow);

                    process.addFlowElement(currentUserTaskFlow);

                    //删除原先节点的入线
                    process.removeFlowElement(sequenceFlowTmp.getId());

//                    recordNewSequence(commandContext, currentUserTaskFlow, currentFlowElement.getExtensionId(),
//                            originalProcessDefinitionEntity.getId());
                } else {
                    currentUserTaskFlowOutList.add(sequenceFlowTmp);
                }

            }
            //新节点的流入
            newUserTask.setIncomingFlows(newTaskincomingFlows);
            //当前节点的流入
            currentFlowElement.setIncomingFlows(currentUserTaskFlowInList);
            //新节点的流出
            newUserTask.setOutgoingFlows(newTaskoutgoingFlows);
            //设置当前节点的流出方向
            currentFlowElement.setOutgoingFlows(currentUserTaskFlowOutList);

            //加入新节点元素
            process.addFlowElement(newUserTask);


            /* 生成BPMN自动布局 */
            new BpmnAutoLayout(bpmnModel).execute();
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
            String processXml = new String(bpmnBytes);
            log.info("向前加签模型:\n {}", processXml);


        } else {

            ParallelGateway parallelGateway = new ParallelGateway();
            parallelGateway.setId(dynamicUserTaskBuilder.nextForkGatewayId(process.getFlowElementMap()));
            process.addFlowElement(parallelGateway);

            UserTask userTask = new UserTask();
            if (dynamicUserTaskBuilder.getId() != null) {
                userTask.setId(dynamicUserTaskBuilder.getId());
            } else {
                userTask.setId(dynamicUserTaskBuilder.nextTaskId(process.getFlowElementMap()));
            }
            dynamicUserTaskBuilder.setDynamicTaskId(userTask.getId());

            userTask.setName(dynamicUserTaskBuilder.getName());
            userTask.setAssignee(dynamicUserTaskBuilder.getAssignee());
            process.addFlowElement(userTask);

            EndEvent endEvent = new EndEvent();
            endEvent.setId(dynamicUserTaskBuilder.nextEndEventId(process.getFlowElementMap()));
            process.addFlowElement(endEvent);

            SequenceFlow flowToUserTask = new SequenceFlow(parallelGateway.getId(), userTask.getId());
            flowToUserTask.setId(dynamicUserTaskBuilder.nextFlowId(process.getFlowElementMap()));
            process.addFlowElement(flowToUserTask);

            SequenceFlow flowFromUserTask = new SequenceFlow(userTask.getId(), endEvent.getId());
            flowFromUserTask.setId(dynamicUserTaskBuilder.nextFlowId(process.getFlowElementMap()));
            process.addFlowElement(flowFromUserTask);

            SequenceFlow initialFlow = initialStartEvent.getOutgoingFlows().get(0);
            initialFlow.setSourceRef(parallelGateway.getId());

            SequenceFlow flowFromStart = new SequenceFlow(initialStartEvent.getId(), parallelGateway.getId());
            flowFromStart.setId(dynamicUserTaskBuilder.nextFlowId(process.getFlowElementMap()));
            process.addFlowElement(flowFromStart);

            GraphicInfo elementGraphicInfo = bpmnModel.getGraphicInfo(initialStartEvent.getId());
            if (elementGraphicInfo != null) {
                double yDiff = 0;
                double xDiff = 80;
                if (elementGraphicInfo.getY() < 173) {
                    yDiff = 173 - elementGraphicInfo.getY();
                    elementGraphicInfo.setY(173);
                }

                Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
                for (String locationId : locationMap.keySet()) {
                    if (initialStartEvent.getId().equals(locationId)) {
                        continue;
                    }

                    GraphicInfo locationGraphicInfo = locationMap.get(locationId);
                    locationGraphicInfo.setX(locationGraphicInfo.getX() + xDiff);
                    locationGraphicInfo.setY(locationGraphicInfo.getY() + yDiff);
                }

                Map<String, List<GraphicInfo>> flowLocationMap = bpmnModel.getFlowLocationMap();
                for (String flowId : flowLocationMap.keySet()) {
                    if (flowFromStart.getId().equals(flowId)) {
                        continue;
                    }

                    List<GraphicInfo> flowGraphicInfoList = flowLocationMap.get(flowId);
                    for (GraphicInfo flowGraphicInfo : flowGraphicInfoList) {
                        flowGraphicInfo.setX(flowGraphicInfo.getX() + xDiff);
                        flowGraphicInfo.setY(flowGraphicInfo.getY() + yDiff);
                    }
                }

                GraphicInfo forkGraphicInfo = new GraphicInfo(elementGraphicInfo.getX() + 75, elementGraphicInfo.getY() - 5, 40, 40);
                bpmnModel.addGraphicInfo(parallelGateway.getId(), forkGraphicInfo);

                bpmnModel.addFlowGraphicInfoList(flowFromStart.getId(), createWayPoints(elementGraphicInfo.getX() + 30, elementGraphicInfo.getY() + 15,
                        elementGraphicInfo.getX() + 75, elementGraphicInfo.getY() + 15));

                GraphicInfo newTaskGraphicInfo = new GraphicInfo(elementGraphicInfo.getX() + 185, elementGraphicInfo.getY() - 163, 80, 100);
                bpmnModel.addGraphicInfo(userTask.getId(), newTaskGraphicInfo);

                bpmnModel.addFlowGraphicInfoList(flowToUserTask.getId(), createWayPoints(elementGraphicInfo.getX() + 95, elementGraphicInfo.getY() - 5,
                        elementGraphicInfo.getX() + 95, elementGraphicInfo.getY() - 123, elementGraphicInfo.getX() + 185, elementGraphicInfo.getY() - 123));

                GraphicInfo endGraphicInfo = new GraphicInfo(elementGraphicInfo.getX() + 335, elementGraphicInfo.getY() - 137, 28, 28);
                bpmnModel.addGraphicInfo(endEvent.getId(), endGraphicInfo);

                bpmnModel.addFlowGraphicInfoList(flowFromUserTask.getId(), createWayPoints(elementGraphicInfo.getX() + 285, elementGraphicInfo.getY() - 123,
                        elementGraphicInfo.getX() + 335, elementGraphicInfo.getY() - 123));
            }
        }

        this.checkBpmnModel(bpmnModel);
        BaseDynamicSubProcessInjectUtil.processFlowElements(commandContext, process, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity);

    }

    @Override
    protected void updateExecutions(CommandContext commandContext,
                                    ProcessDefinitionEntity processDefinitionEntity,
                                    ExecutionEntity executionEntity,
                                    List<ExecutionEntity> childExecutions) {
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        List<ExecutionEntity> oldExecution = executionEntityManager.findChildExecutionsByProcessInstanceId(processInstanceId);
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionEntity.getId());

        TaskService taskService = CommandContextUtil.getTaskService(commandContext);
        List<TaskEntity> taskEntities = taskService.findTasksByProcessInstanceId(processInstanceId);
        // 删除当前活动任务
        for (TaskEntity taskEntity : taskEntities) {
            taskEntity.getIdentityLinks().stream().forEach(identityLinkEntity -> {
                if (identityLinkEntity.isGroup()) {
                    taskEntity.deleteGroupIdentityLink(identityLinkEntity.getGroupId(), "candidate");
                } else {
                    taskEntity.deleteUserIdentityLink(identityLinkEntity.getUserId(), "participant");
                }
            });
            if (taskEntity.getTaskDefinitionKey().equals(currentFlowElement.getId())) {
                taskService.deleteTask(taskEntity, false);
            }
        }
        //设置活动后的节点
        UserTask userTask = (UserTask) bpmnModel.getProcessById(processDefinitionEntity.getKey()).getFlowElement(dynamicUserTaskBuilder.getId());
        ExecutionEntity execution = executionEntityManager.createChildExecution(executionEntity);
        execution.setCurrentFlowElement(userTask);
        Context.getAgenda().planContinueProcessOperation(execution);

        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
        String processXml = new String(bpmnBytes);
        log.info("向前加签bpm:\n{}", processXml);

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
