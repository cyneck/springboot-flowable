package com.cyneck.workflow.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cyneck.workflow.common.BusinessRuntimeException;
import com.cyneck.workflow.common.Result;
import com.cyneck.workflow.common.ResultUtil;
import com.cyneck.workflow.model.dto.ProcessDefinitionDto;
import com.cyneck.workflow.model.dto.ProcessInstanceCreateReq;
import com.cyneck.workflow.service.impl.FlowableBpmnModelServiceImpl;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiSort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description:流程实例控制器 </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:15
 **/
@Api(tags = "3-流程实例接口", value = "InstanceController")
@ApiSort(value = 3)
@Transactional
@RestController
@RequestMapping("/api/runtime/processInstances/")
@Slf4j
public class InstanceController extends ApiController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    // 任务管理服务
    @Autowired
    private TaskService taskService;

    @Autowired
    private FormService formService;

    @Autowired
    private HistoryService historyService;

    // 流程引擎
    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    FlowableBpmnModelServiceImpl flowableBpmnModelService;

    @ApiOperation(value = "3.1-发起流程实例（用户定义form数据）")
    @ApiOperationSupport(order = 1)
    @PostMapping(value = "startProcessInstanceByForm")
    public Result startProcessInstanceByForm(@RequestBody ProcessInstanceCreateReq formData) {
        //设置流程发起人
        ProcessInstance processInstance;
        Authentication.setAuthenticatedUserId(formData.getUserId());
        if (StringUtils.isNotBlank(formData.getProcessDefinitionId())) {
            processInstance = runtimeService.startProcessInstanceById(formData.getProcessDefinitionId(),
                    formData.getBusinessKey(),
                    formData.getFormData());
        } else if (StringUtils.isNotBlank(formData.getProcessDefinitionKey())) {
            processInstance = runtimeService.startProcessInstanceByKey(formData.getProcessDefinitionKey(),
                    formData.getBusinessKey(),
                    formData.getFormData());
        } else {
            throw new BusinessRuntimeException("数据异常，流程不能发起！");
        }
        // 这个方法最终使用一个ThreadLocal类型的变量进行存储，也就是与当前的线程绑定，
        // 所以流程实例启动完毕之后，需要设置为null，防止多线程的时候出问题。
        Authentication.setAuthenticatedUserId(null);
        HashMap result = new HashMap();
        result.put("processDefinitionKey", processInstance.getProcessDefinitionKey());
        result.put("processInstanceId", processInstance.getId());
        return ResultUtil.data(result);
    }

    @ApiOperation("3.2-获取所有流程定义")
    @ApiOperationSupport(order = 2)
    @GetMapping(value = "getAllProcess")
    public Result getAllProcess() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        List<ProcessDefinitionDto> result = new ArrayList<>();
        ProcessDefinitionDto processDefinitionDto;
        for (ProcessDefinition processDefinition : list) {
            processDefinitionDto = new ProcessDefinitionDto();
            String formKey = formService.getStartFormKey(processDefinition.getId());
            processDefinitionDto = processDefinitionDto.createProcessDefinitionResponse(processDefinition, formKey);
            result.add(processDefinitionDto);
        }
        return ResultUtil.data(result);
    }

    @ApiOperation("3.3-获取所有已发布流程")
    @ApiOperationSupport(order = 3)
    @GetMapping(value = "getPublishedProcess")
    public Result getPublishedProcess() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .latestVersion()//查询最后的版本
//                .processDefinitionName("testStr.bpmn20.xml")//name
                .active()
                .list();
        List<ProcessDefinitionDto> result = new ArrayList<>();
        ProcessDefinitionDto processDefinitionDto;
        for (ProcessDefinition processDefinition : list) {
            processDefinitionDto = new ProcessDefinitionDto();
            String formKey = formService.getStartFormKey(processDefinition.getId());
            processDefinitionDto = processDefinitionDto.createProcessDefinitionResponse(processDefinition, formKey);
            result.add(processDefinitionDto);
        }
        return ResultUtil.data(result);
    }

    @ApiOperation("3.4-根据ID获取流程定义")
    @ApiOperationSupport(order = 4)
    @GetMapping(value = "getProcessDefinitionById", name = "根据ID获取流程定义")
    public Result getProcessDefinitionById(String processDefinitionId) {
        // 直接查询数据库，不查询缓存，防止出现挂起激活验证不一致
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        String formKey = formService.getStartFormKey(processDefinition.getId());
        ProcessDefinitionDto processDefinitionDto = new ProcessDefinitionDto();
        return ResultUtil.data(processDefinitionDto.createProcessDefinitionResponse(processDefinition, formKey));
    }

    @ApiOperation("3.5-根据key获取流程定义")
    @ApiOperationSupport(order = 5)
    @GetMapping(value = "getProcessDefinitionByKey", name = "根据key获取流程定义")
    public Result getProcessDefinitionByKey(String processDefinitionKey) {
        // 直接查询数据库，不查询缓存，防止出现挂起激活验证不一致
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey).list();
        List<ProcessDefinitionDto> result = new ArrayList<>();
        ProcessDefinitionDto processDefinitionDto;
        for (ProcessDefinition processDefinition : processDefinitionList) {
            processDefinitionDto = new ProcessDefinitionDto();
            String formKey = formService.getStartFormKey(processDefinition.getId());
            processDefinitionDto = processDefinitionDto.createProcessDefinitionResponse(processDefinition, formKey);
            result.add(processDefinitionDto);
        }
        return ResultUtil.data(result);
    }

    @ApiOperation("3.6-流程打印")
    @ApiOperationSupport(order = 6)
    @PostMapping(value = "processDiagram")
    public void processDiagram(HttpServletResponse httpServletResponse,
                               String processInstanceId) {
        String processDefinitionId = "";
        if (this.isFinished(processInstanceId)) {// 如果流程已经结束，则得到结束节点
            HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            processDefinitionId = pi.getProcessDefinitionId();
        } else {// 如果流程没有结束，则取当前活动节点
            // 根据流程实例ID获得当前处于活动状态的ActivityId合集
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            processDefinitionId = pi.getProcessDefinitionId();
        }
        List<String> highLightedActivitis = new ArrayList<String>();

        /**
         * 获得活动的节点
         */
        List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();

        for (HistoricActivityInstance tempActivity : highLightedActivitList) {
            String activityId = tempActivity.getActivityId();
            highLightedActivitis.add(activityId);
        }

        List<String> flows = new ArrayList<>();
        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
        String processXml = new String(bpmnBytes);
        log.info("打印图片bpm:\n{}", processXml);
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();

        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "bmp", highLightedActivitis, flows, engconf.getActivityFontName(),
                engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0, true);
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        } catch (IOException e) {
            log.error("操作异常", e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
    }

    @ApiOperation("3.7-获取流程操作记录")
    @ApiOperationSupport(order = 7)
    @PostMapping(value = "getProcessHistory")
    public Result getProcessHistory(String processInstanceId) {
        List<String> filterEvents = null;
        //过滤历史节点类型 只要开始 结束 任务节点类型的
        if (CollectionUtil.isEmpty(filterEvents)) {
            filterEvents = Lists.newArrayList("userTask");
        }
        List<String> activityTypeFilter = filterEvents;
        //添加查询条件
        List<HistoricActivityInstance> activities =
                historyService.createHistoricActivityInstanceQuery()
                        //选择特定实例
                        .processInstanceId(processInstanceId)
                        //选择已完成的
                        .finished()
                        //根据实例完成时间升序排列
                        //.orderByHistoricActivityInstanceEndTime().asc()
                        .list();
        List<HistoricActivityInstance> result = activities.stream().filter(his -> activityTypeFilter.contains(his.getActivityType()))
                .sorted(Comparator.comparing(HistoricActivityInstance::getStartTime))//排序
                .collect(Collectors.toList());
        return ResultUtil.data(result);
    }

    @ApiOperation("3.8-获取指定流程信息")
    @ApiOperationSupport(order = 8)
    @PostMapping(value = "getProcessInstanceId")
    public Result getProcessInstanceId(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        Object result = JSONUtil.parse(JSONUtil.toJsonStr(processInstance));
        return ResultUtil.data(result);
    }

    @ApiOperation("3.9-获得指定流程的所有节点")
    @ApiOperationSupport(order = 9)
    @PostMapping(value = "getProcessUserTaskList")
    public Result getProcessUserTaskList(String processDefinitionId) {
        BpmnModel bpmnModel = flowableBpmnModelService.getBpmnModelByProcessDefId(processDefinitionId);
        // 获得所有流程定的节点信息
        List<UserTask> userTasks = flowableBpmnModelService.findUserTasksByBpmnModel(bpmnModel);
        List<EndEvent> endEvents = flowableBpmnModelService.findEndFlowElement(processDefinitionId);
        UserTask firstNode = new UserTask();
        firstNode.setName("开始");
        userTasks.add(0, firstNode);

        if (endEvents.size() >= 1) {
            UserTask endNode = new UserTask();
            endNode.setName(endEvents.get(0).getName());
            userTasks.add(endNode);
        }

        JSONArray arr = new JSONArray();
        userTasks.forEach(s -> {
            JSONObject obj = new JSONObject();
            obj.put("id", s.getId());
            obj.put("executionId", s.getExtensionId());
            obj.put("assignee", s.getAssignee());//签收人或被委托
            obj.put("durationInMillis", s.getDueDate()); //审核过程耗时
            obj.put("name", s.getName());
            obj.put("owner", s.getOwner());//实际签收人 任务的拥有者
            arr.add(obj);
        });
        return ResultUtil.data(arr);
    }


    /**
     * 获取流程节点顺序list
     *
     * @param processInstanceId
     * @param taskList
     * @param flowElements
     * @param workflowRequestFormData
     * @param curFlowElement
     */
    private void getTaskList(String processInstanceId,
                             List<Object> taskList,
                             Collection<FlowElement> flowElements,
                             Map<String, Object> workflowRequestFormData,
                             FlowElement curFlowElement) {
        if (curFlowElement == null && taskList.size() == 0) {
            // 获取第一个UserTask
            FlowElement startElement = flowElements.stream().filter(flowElement -> flowElement instanceof StartEvent).collect(Collectors.toList()).get(0);
            List<SequenceFlow> outgoingFlows = ((StartEvent) startElement).getOutgoingFlows();
            String targetRef = outgoingFlows.get(0).getTargetRef();
            // 根据ID找到FlowElement
            FlowElement targetElementOfStartElement = getFlowElement(flowElements, targetRef);
            if (targetElementOfStartElement instanceof UserTask) {
                this.getTaskList(processInstanceId, taskList, flowElements, workflowRequestFormData, targetElementOfStartElement);
            }

        } else if (curFlowElement instanceof UserTask) {
            // 只有Usertask才添加到列表中
            taskList.add(curFlowElement);
            String targetRef = "";
            List<SequenceFlow> outgoingFlows = ((UserTask) curFlowElement).getOutgoingFlows();
            if (outgoingFlows.size() == 1) {
                targetRef = outgoingFlows.get(0).getTargetRef();
            } else {
                // 找到表达式成立的sequenceFlow的
                SequenceFlow sequenceFlow = null;
                if (sequenceFlow != null) {
                    targetRef = sequenceFlow.getTargetRef();
                }
            }
            // 根据ID找到FlowElement
            FlowElement targetElement = getFlowElement(flowElements, targetRef);

            this.getTaskList(processInstanceId, taskList, flowElements, workflowRequestFormData, targetElement);
        } else if (curFlowElement instanceof ExclusiveGateway) {
            String targetRef = "";
            // 如果为排他网关，获取符合条件的sequenceFlow的目标FlowElement
            List<SequenceFlow> exclusiveGatewayOutgoingFlows = ((ExclusiveGateway) curFlowElement).getOutgoingFlows();
            // 找到表达式成立的sequenceFlow的
//            SequenceFlow sequenceFlow = getSequenceFlow(workflowRequestFormData, exclusiveGatewayOutgoingFlows);
            SequenceFlow sequenceFlow = null;
            if (sequenceFlow != null) {
                targetRef = sequenceFlow.getTargetRef();
            }
            // 根据ID找到FlowElement
            FlowElement targetElement = getFlowElement(flowElements, targetRef);

            this.getTaskList(processInstanceId, taskList, flowElements, workflowRequestFormData, targetElement);
        }
    }

    public boolean isFinished(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().finished()
                .processInstanceId(processInstanceId).count() > 0;
    }


    /***
     * 根据ID找到FlowElement
     * @param flowElements
     * @param targetRef
     * @return
     */
    private FlowElement getFlowElement(Collection<FlowElement> flowElements, String targetRef) {
        List<FlowElement> targetFlowElements = flowElements.stream().filter(
                flowElement -> !StringUtils.isEmpty(flowElement.getId()) && flowElement.getId().equals(targetRef)
        ).collect(Collectors.toList());
        if (targetFlowElements.size() > 0) {
            return targetFlowElements.get(0);
        }
        return null;
    }

}
