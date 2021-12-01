package com.cyneck.workflow.service.impl;

import com.cyneck.workflow.service.IFlowableBpmnModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/2 16:11
 **/
@Service
public class FlowableBpmnModelServiceImpl implements IFlowableBpmnModelService {

    @Autowired
    protected ManagementService managementService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected IdentityService identityService;


    @Override
    public BpmnModel getBpmnModelByProcessDefId(String processDefId) {
        return repositoryService.getBpmnModel(processDefId);
    }

    public List<UserTask> findUserTasksByBpmnModel(BpmnModel bpmnModel) {
        List<UserTask> datas = new ArrayList<>();
        List<Process> processes = bpmnModel.getProcesses();
        processes.forEach(process -> {
            List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
            datas.addAll(userTasks);
        });
        return datas;
    }

    public List<FlowNode> findFlowNodes(String processDefId) {
        List<FlowNode> flowNodes = new ArrayList<>();
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(processDefId);
        org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
        Collection<FlowElement> list = process.getFlowElements();
        list.forEach(flowElement -> {
            if (flowElement instanceof FlowNode) {
                flowNodes.add((FlowNode) flowElement);
            }
        });
        return flowNodes;
    }

    @Override
    public List<EndEvent> findEndFlowElement(String processDefId) {
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(processDefId);
        if (bpmnModel != null) {
            org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
            return process.findFlowElementsOfType(EndEvent.class);
        } else {
            return null;
        }
    }

    @Override
    public FlowNode findMainProcessActivityByActivityId(String processDefId, String activityId) {
        FlowNode activity = null;
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(processDefId);
        org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
        FlowElement flowElement = process.getFlowElement(activityId);
        if (flowElement != null) {
            activity = (FlowNode) flowElement;
        }
        return activity;
    }

    @Override
    public FlowNode findFlowNodeByActivityId(String processDefId, String activityId) {
        FlowNode activity = null;
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(processDefId);
        List<org.flowable.bpmn.model.Process> processes = bpmnModel.getProcesses();
        for (org.flowable.bpmn.model.Process process : processes) {
            FlowElement flowElement = process.getFlowElementMap().get(activityId);
            if (flowElement != null) {
                activity = (FlowNode) flowElement;
                break;
            }
        }
        return activity;
    }

    @Override
    public boolean checkActivitySubprocessByActivityId(String processDefId, String activityId) {
        boolean flag = true;
        List<FlowNode> activities = this.findFlowNodesByActivityId(processDefId, activityId);
        if (CollectionUtils.isNotEmpty(activities)) {
            flag = false;
        }
        return flag;
    }

    public List<FlowNode> findFlowNodesByActivityId(String processDefId, String activityId) {
        List<FlowNode> activities = new ArrayList<>();
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(processDefId);
        List<org.flowable.bpmn.model.Process> processes = bpmnModel.getProcesses();
        for (org.flowable.bpmn.model.Process process : processes) {
            FlowElement flowElement = process.getFlowElement(activityId);
            if (flowElement != null) {
                FlowNode flowNode = (FlowNode) flowElement;
                activities.add(flowNode);
            }
        }
        return activities;
    }

    @Override
    public List<Activity> findActivityByActivityId(String processDefId, String activityId) {
        List<Activity> activities = new ArrayList<>();
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(processDefId);
        List<org.flowable.bpmn.model.Process> processes = bpmnModel.getProcesses();
        for (org.flowable.bpmn.model.Process process : processes) {
            FlowElement flowElement = process.getFlowElement(activityId);
            if (flowElement != null) {
                Activity activity = (Activity) flowElement;
                activities.add(activity);
            }
        }
        return activities;
    }

    @Override
    public Activity findActivityByName(String processDefId, String name) {
        Activity activity = null;
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(processDefId);
        Process process = bpmnModel.getMainProcess();
        Collection<FlowElement> list = process.getFlowElements();
        for (FlowElement f : list) {
            if (StringUtils.isNotBlank(name)) {
                if (name.equals(f.getName())) {
                    activity = (Activity) f;
                    break;
                }
            }
        }
        return activity;
    }

}
