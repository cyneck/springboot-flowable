package com.cyneck.workflow.controller;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyneck.workflow.common.*;
import com.cyneck.workflow.core.AddCcIdentityLinkCmd;
import com.cyneck.workflow.core.AfterSignUserTaskCmd;
import com.cyneck.workflow.core.BeforeSignUserTaskCmd;
import com.cyneck.workflow.core.CompleteTaskReadCmd;
import com.cyneck.workflow.mapper.IdentitylinkMapper;
import com.cyneck.workflow.model.dto.ExecuteTaskReq;
import com.cyneck.workflow.model.dto.TaskDetailDto;
import com.cyneck.workflow.model.dto.TaskEditRequest;
import com.cyneck.workflow.model.dto.TaskReq;
import com.cyneck.workflow.service.impl.FlowableBpmnModelServiceImpl;
import com.cyneck.workflow.utils.FlowableUtils;
import com.cyneck.workflow.common.*;
import com.cyneck.workflow.model.dto.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiSort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.TaskServiceImpl;
import org.flowable.engine.impl.dynamic.DynamicUserTaskBuilder;
import org.flowable.engine.impl.persistence.entity.AttachmentEntityImpl;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Attachment;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.rest.service.api.runtime.task.TaskResponse;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 流程任务rest接口</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:08
 **/
@Api(tags = "4-任务处理接口", value = "ExecutionTaskController")
@ApiSort(value = 4)
@Transactional
@Slf4j
@RestController
@RequestMapping("/api/runtime/task/")
public class TaskController extends ApiController {

    @Autowired
    private RuntimeService runtimeService;

    @Resource
    private TaskServiceImpl taskService;
    @Autowired
    private HistoryService historyService;

    // 流程引擎
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    protected FormService formService;

    @Autowired
    protected ManagementService managementService;

    @Autowired
    IdentitylinkMapper identitylinkMapper;

    @Autowired
    FlowableBpmnModelServiceImpl flowableBpmnModelService;

    @ApiOperation("4.1-查询用户待办数量")
    @ApiOperationSupport(order = 1)
    @GetMapping(value = "count")
    public Result count(@RequestParam(required = true) String userId,
                        @RequestParam(required = false) String[] groupIds) {
        long count = 0;
        if (groupIds.length == 0) {
            count = taskService.createTaskQuery().taskCandidateOrAssigned(userId).orderByTaskPriority().desc()
                    .orderByTaskCreateTime().asc()
                    .count();
        } else {
            count = taskService.createTaskQuery().taskCandidateOrAssigned(userId).taskCandidateGroupIn(Arrays.asList(groupIds.clone())).orderByTaskPriority().desc()
                    .orderByTaskCreateTime().asc()
                    .count();
        }
        return new ResultUtil<>().setData(count);
    }

    @ApiOperation("4.2-查询代办（条件分页）")
    @ApiOperationSupport(order = 2)
    @PostMapping(value = "listTodo")
    public Result listTodo(@RequestBody TaskReq query) {
        int firstResult = (int) ((query.getCurrent() - 1) * query.getSize());
        int maxResults = (int) (query.getCurrent() * query.getSize());
        TaskQuery taskQuery = taskService.createTaskQuery();// 查询条件
        if (StringUtils.isNotBlank(query.getUserId())) {  //代办人ID
            taskQuery.taskCandidateOrAssigned(query.getUserId());
        }
        if (query.getGroupIds() != null && query.getGroupIds().size() > 0) { //代办分组ID,例：["ROLE_ADMIN", "ROLE_GROUP_LEADER"]
            taskQuery.taskCandidateGroupIn(query.getGroupIds());
        }
        if (StringUtils.isNotBlank(query.getBusinessKey())) {
            taskQuery.processInstanceBusinessKey(query.getBusinessKey());
        }
        if (StringUtils.isNotBlank(query.getProcessInstanceId())) {
            taskQuery.processInstanceId(query.getProcessInstanceId());
        }
        if (StringUtils.isNotBlank(query.getProcessDefinitionKey())) {
            taskQuery.processDefinitionKey(query.getProcessDefinitionKey());
        }
        List<Task> taskList = taskQuery
                .taskCategory(FlowableConstant.CATEGORY_TODO)
                .includeProcessVariables()//在任务查询结果中包含全局任务变量
                .orderByTaskPriority().desc() //通过 TaskPriority 排序
                .orderByTaskCreateTime().desc() //通过 TaskCreateTime 排序
                .listPage(firstResult, maxResults);
        JSONArray arr = new JSONArray();
        taskList.forEach(task -> {
            JSONObject obj = new JSONObject();
            obj.put("id", task.getId());
            obj.put("assignee", task.getAssignee());
            obj.put("name", task.getName());
            obj.put("variables", task.getProcessVariables());
            obj.put("createTime", task.getCreateTime());
            obj.put("processInstanceId", task.getProcessInstanceId());//查询流程图
            obj.put("executionId", task.getExecutionId());
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            String businessKey = processInstance.getBusinessKey();
            String businessName = processInstance.getName();
            String processDefinitionId = processInstance.getProcessDefinitionId();
            String processDefinitionKey = processInstance.getProcessDefinitionKey();
            String processDefinitionName = processInstance.getProcessDefinitionName();
            obj.put("businessKey", businessKey);
            obj.put("businessName", businessName);
            obj.put("processDefinitionId", processDefinitionId);
            obj.put("processDefinitionKey", processDefinitionKey);
            obj.put("processDefinitionName", processDefinitionName);
            arr.add(obj);
        });
        Long count = taskQuery.count();
        Page page = new Page(query.getCurrent(), query.getSize(), count);
        page.setRecords(arr);
        return ResultUtil.data(page);
    }

    @ApiOperation("4.3-查询任务详情")
    @ApiOperationSupport(order =3)
    @GetMapping(value = "detail", name = "查询任务详情")
    public TaskDetailDto detail(@RequestParam("taskId") String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        Task task = null;
        if (historicTaskInstance.getEndTime() == null) {
            task = taskService.createTaskQuery().taskId(taskId).singleResult();
        }
        String formKey = formService.getTaskFormKey(historicTaskInstance.getProcessDefinitionId(), historicTaskInstance.getTaskDefinitionKey());

        TaskDetailDto result = new TaskDetailDto();
        result.setAssigneeName(historicTaskInstance.getAssignee());
        result.setOwnerName(historicTaskInstance.getOwner());
        result.setDelegationState(task.getDelegationState());
        result.setSuspended(task.isSuspended());
        result.setFormKey(formKey);
        result.setId(historicTaskInstance.getId());
        result.setProcessDefinitionId(historicTaskInstance.getProcessDefinitionId());
        result.setTaskDefinitionKey(historicTaskInstance.getTaskDefinitionKey());
        result.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
        result.setTenantId(historicTaskInstance.getTenantId());
        result.setDescription(historicTaskInstance.getDescription());
        result.setStartTime(historicTaskInstance.getStartTime());
        result.setEndTime(historicTaskInstance.getEndTime());
        result.setDueDate(historicTaskInstance.getDueDate());
        result.setPriority(historicTaskInstance.getPriority());
        result.setName(historicTaskInstance.getName());
        result.setCategory(historicTaskInstance.getCategory());
        return result;
    }

    @ApiOperation("4.4-查询已办（条件分页）")
    @ApiOperationSupport(order = 4)
    @PostMapping(value = "listDone")
    public Result listDone(@RequestBody TaskReq taskReq) {
        String userId = taskReq.getUserId();
        int firstResult = (int) ((taskReq.getCurrent() - 1) * taskReq.getSize());
        int maxResults = (int) (taskReq.getCurrent() * taskReq.getSize());
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();

        if (StringUtils.isNotBlank(userId)) {
            query.taskAssignee(userId).or().taskOwner(userId);
        }
        if (StringUtils.isNotBlank(taskReq.getBusinessKey())) {
            query.processInstanceBusinessKey(taskReq.getBusinessKey());
        }
        if (StringUtils.isNotBlank(taskReq.getBusinessName())) {
            query.processVariableValueEquals("businessName", taskReq.getBusinessName());
        }
        if (StringUtils.isNotBlank(taskReq.getBusinessType())) {
            query.processVariableValueLike("businessType", taskReq.getBusinessType());
        }
        if (StringUtils.isNotBlank(taskReq.getProcessDefinitionKey())) {
            query.processVariableValueLike("processDefinitionKey", taskReq.getProcessDefinitionKey());
        }
        List<HistoricTaskInstance> historicTaskInstances = query.finished()
//                .includeProcessVariables()
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .listPage(firstResult, maxResults);
        JSONArray arr = new JSONArray();
        historicTaskInstances.forEach(historicTaskInstance -> {
            JSONObject obj = new JSONObject();
            obj.put("id", historicTaskInstance.getId());
            obj.put("executionId", historicTaskInstance.getExecutionId());
            obj.put("processInstanceId", historicTaskInstance.getProcessInstanceId());
            obj.put("processDefinitionId", historicTaskInstance.getProcessDefinitionId());
            obj.put("taskDefinitionId", historicTaskInstance.getTaskDefinitionId());
            obj.put("assignee", historicTaskInstance.getAssignee());//签收人或被委托
            obj.put("createTime", historicTaskInstance.getCreateTime());
            obj.put("endTime", historicTaskInstance.getEndTime());
            obj.put("durationInMillis", historicTaskInstance.getDurationInMillis()); //审核过程耗时
            obj.put("name", historicTaskInstance.getName());
            obj.put("owner", historicTaskInstance.getOwner());//实际签收人 任务的拥有者
            //查询业务主键
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult();
            obj.put("businessKey", processInstance.getBusinessKey());
            obj.put("businessName", processInstance.getName());
            obj.put("processDefinitionKey", processInstance.getProcessDefinitionKey());
            obj.put("processDefinitionName", processInstance.getProcessDefinitionName());
            arr.add(obj);
        });
        long count = query.count();
        Page page = new Page(taskReq.getCurrent(), taskReq.getSize(), count);
        page.setRecords(arr);
        return new ResultUtil<>().setData(page);
    }

    @ApiOperation("4.5-查询全部（条件）")
    @ApiOperationSupport(order = 5)
    @PostMapping(value = "list")
    public Result list(@RequestBody TaskReq taskReq) throws Exception {
        int firstResult = (int) ((taskReq.getCurrent() - 1) * taskReq.getSize());
        int maxResults = (int) (taskReq.getCurrent() * taskReq.getSize());
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
        if (ObjectUtils.isNotEmpty(taskReq.getTaskId())) {
            query.taskId(taskReq.getTaskId());
        }
        if (StringUtils.isNotBlank(taskReq.getProcessInstanceId())) {
            query.processInstanceId(taskReq.getProcessInstanceId());
        }
        if (StringUtils.isNotBlank(taskReq.getUserId())) {
            query.taskOwner(taskReq.getUserId()).or().taskAssignee(taskReq.getTaskId());
        }
        List list = query.listPage(firstResult, maxResults);
        JSONArray array = new JSONArray();
        list.forEach(s -> array.add(JSONUtil.parseObj(s, true)));
        long count = query.count();
        Page page = new Page(taskReq.getCurrent(), taskReq.getSize(), count);
        page.setRecords(array);
        return ResultUtil.data(array);
    }

    @ApiOperation("4.6-终止流程")
    @ApiOperationSupport(order = 6)
    @PostMapping(value = "stopProcessInstance")
    public Result stopProcessInstance(String processInstanceId, String userId, String taskId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        if (bpmnModel != null) {
            Process process = bpmnModel.getMainProcess();
            List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class, false);
            if (endNodes != null && endNodes.size() > 0) {
                taskService.addComment(taskId, processInstanceId, ActionEnum.STOP.getAction(), "终止流程");
                String endId = endNodes.get(0).getId();
                List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstance.getProcessInstanceId()).list();
                List<String> executionIds = new ArrayList<>();
                executions.forEach(execution -> executionIds.add(execution.getId()));
                runtimeService.createChangeActivityStateBuilder().moveExecutionsToSingleActivityId(executionIds, endId).changeState();
            }
        }
        return ResultUtil.success("终止流程成功");
    }

    @ApiOperation("4.7-流程中，任务执行详情")
    @ApiOperationSupport(order = 7)
    @GetMapping(value = "records/{processInstanceId}")
    public Result records(@PathVariable String processInstanceId) {
        List<String> filterEvents = null;
        //过滤历史节点类型 只要开始 结束 任务节点类型的
        if (CollectionUtil.isEmpty(filterEvents)) {
            filterEvents = Lists.newArrayList("startEvent", "endEvent", "userTask");
        }
        List<String> activityTypeFilter = filterEvents;
        List<HistoricActivityInstance> collect = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .desc()
                .list()
                .stream()
                .filter(his -> activityTypeFilter.contains(his.getActivityType()))
                .collect(Collectors.toList());
        // 增加备注信息
        List list = new ArrayList();
        for (HistoricActivityInstance historic : collect) {
            Map map = new HashMap();
            List<Comment> taskComments = taskService.getTaskComments(historic.getTaskId());
            map.put("historic", historic);
            map.put("comments", taskComments);
            list.add(map);
        }
        return new ResultUtil<>().setData(list);
    }

    @ApiOperation("4.8-任务修改")
    @ApiOperationSupport(order = 8)
    @RequestMapping(value = "edit", name = "任务修改", method = RequestMethod.PUT)
    public TaskResponse edit(@RequestBody TaskEditRequest taskEditRequest) {
        Task task = taskService.createTaskQuery().taskId(taskEditRequest.getId()).singleResult();
        task.setName(taskEditRequest.getName());
        task.setDescription(taskEditRequest.getDescription());
        task.setAssignee(taskEditRequest.getAssignee());
        task.setOwner(taskEditRequest.getOwner());
        task.setDueDate(taskEditRequest.getDueDate());
        task.setPriority(taskEditRequest.getPriority());
        task.setCategory(taskEditRequest.getCategory());
        taskService.saveTask(task);

        TaskResponse result = new TaskResponse();
        result.setId(task.getId());
        result.setName(task.getName());
        result.setOwner(task.getOwner());
        result.setTaskDefinitionKey(task.getTaskDefinitionKey());
        result.setCreateTime(task.getCreateTime());
        result.setAssignee(task.getAssignee());
        result.setDescription(task.getDescription());
        result.setDueDate(task.getDueDate());
        result.setDelegationState(task.getDelegationState().name());
        result.setFormKey(task.getFormKey());
        result.setParentTaskId(task.getParentTaskId());
        result.setPriority(task.getPriority());
        result.setSuspended(task.isSuspended());
        result.setTenantId(task.getTenantId());
        result.setCategory(task.getCategory());
        result.setProcessDefinitionId(task.getProcessDefinitionId());
        result.setProcessInstanceId(task.getProcessInstanceId());
        return result;
    }

    @ApiOperation("4.9-获取历史流程")
    @ApiOperationSupport(order = 9)
    @GetMapping("getHistoryProcess")
    public Result getHistoryProcess(String processDefinitionKey) {
        List<HistoricProcessInstance> activities =
                historyService.createHistoricProcessInstanceQuery()
                        .processDefinitionKey(processDefinitionKey)
                        .list();
        return ResultUtil.data(activities);
    }

    //任务执行类型 claim：签收 unclaim 反签收 complete 完成 delegate 任务委派 resolve 任务签收完成 返回任务人 assignee 任务转办
    @ApiOperation("4.10-执行任务")
    @ApiOperationSupport(order = 10)
    @PostMapping(value = "executeTask")
    public Result executeTask(@RequestBody ExecuteTaskReq executeTaskReq) {
        String action = executeTaskReq.getAction();
        String userId = executeTaskReq.getUserId();
        String assigneeId = userId;
        if (StringUtils.isNotBlank(executeTaskReq.getAssigneeIds())) {
            String[] assigneeIdArray = executeTaskReq.getAssigneeIds().split(",");
            assigneeId = assigneeIdArray[0];
        }
        String assigneeGroups = executeTaskReq.getAssigneeGroups();
        Boolean localScope = executeTaskReq.getLocalScope();
        Map<String, Object> variables = executeTaskReq.getVariables();
        log.info("-----签收任务ID:{},签收类型:{},签收人ID:{},---------", executeTaskReq.getTaskId(), action, userId);
        Task task = taskService.createTaskQuery().taskId(executeTaskReq.getTaskId()).singleResult();
        Assert.isTrue(task != null, "taskId:[" + executeTaskReq.getTaskId() + "]任务不存在");
        if (StringUtils.isNotBlank(executeTaskReq.getComment())) {
            Comment comment = taskService.addComment(executeTaskReq.getTaskId(),
                    task.getProcessInstanceId(),
                    executeTaskReq.getComment());
            comment.setUserId(userId);
            taskService.saveComment(comment);
        }
        Map<String, Object> result = Maps.newHashMap();
        ActionEnum actionEnum = ActionEnum.actionOf(action);
        switch (actionEnum) {
            case COMPLETE:
                //通过任务
                result = complete(task, userId, variables, localScope);
                break;
            case CLAIM:
                //认领签收任务
                this.claim(task.getId(), assigneeId);
                break;
            case UNCLAIM:
                //反签收
                this.unClaim(task.getId());
                break;
            case DELEGATE:
                //任务委派
                this.delegate(task.getId(), assigneeId);
                break;
            case RESOLVE:
                //委派任务完成，归还委派人
                this.resolve(task.getId());
                break;
            case ASSIGNEE:
                //任务转办
                this.setAssignee(task.getId(), assigneeId);
                break;
            case SIGN_BACKWARD:
                //向后加签
                this.signBackward(task.getId(), userId);
                break;
            case SIGN_FORWARD:
                //向前加签
                this.signForward(task.getId(), userId);
                break;
            case TRACK:
                //跟踪
                this.track(task.getId(), userId);
                break;
            case READ:
                //已阅
                this.read(task.getId(), userId);
                break;
            case TAKE_BACK:
                //撤回
                this.takeBack(task.getProcessInstanceId(), task.getId(), userId);
                break;
            case CARBON_COPY:
                this.carbonCopy(task, userId, executeTaskReq.getAssigneeIds().split(","));
            default:
                break;
        }
        return new ResultUtil<>().setData(result);
    }

    @ApiOperation("4.11-催办")
    @ApiOperationSupport(order = 11)
    @PostMapping(value = "press")
    @Transactional
    public Result pressUser(@RequestParam("taskId") String taskId,
                            @RequestParam("userId") String userId,
                            @RequestParam("callUrl") String callUrl) {
        press(taskId, userId, callUrl);
        return ResultUtil.success("ok");
    }

    @ApiOperation("4.12-添加附件信息")
    @ApiOperationSupport(order = 12)
    @PostMapping(value = "addAttachment")
    public Result addAttachment(@RequestBody AttachmentEntityImpl attachment) {
        log.info("----任务或者流程实例添加备注：任务ID:{},流程实例ID{}---------", attachment.getUserId(), attachment.getProcessInstanceId());
        Task task = taskService.createTaskQuery().taskId(attachment.getTaskId()).singleResult();
        if (task == null) {
            throw new BusinessRuntimeException("taskService：" + attachment.getTaskId() + "的taskId未查询到");
        }
        Attachment attachmentTmp = taskService.createAttachment(FileNameUtil.extName(attachment.getName()),
                attachment.getTaskId(),
                attachment.getProcessInstanceId(),
                attachment.getName(),
                attachment.getDescription(),
                attachment.getUrl());
        taskService.saveAttachment(attachmentTmp);
        return ResultUtil.data(attachmentTmp, "添加附件信息成功！");
    }

    @ApiOperation("4.13-查询批注信息")
    @ApiOperationSupport(order = 13)
    @GetMapping(value = "getTaskComments")
    public Result getTaskComments(String processInstanceId) {
        List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);
        return new ResultUtil<>().setData(comments);
    }


    @ApiOperation("4.14-任务撤回")
    @ApiOperationSupport(order = 14)
    @RequestMapping(value = "takeBack", name = "任务撤回", method = RequestMethod.PUT)
    public Result takeBack(String processInstanceId, String currentTaskId, String targetTaskId) {
        log.info("----任务撤回：流程实例ID:{},当前活动任务ID:{},撤回到达的任务ID:{}，---------", processInstanceId, currentTaskId, targetTaskId);
        try {
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(processInstanceId)
                    .moveActivityIdTo(currentTaskId, targetTaskId)
                    .changeState();
        } catch (FlowableObjectNotFoundException e) {
            log.info("任务撤回报错：{}", e);
        }
        return new ResultUtil<>().setSuccessMsg("任务撤回成功");
    }


    @ApiOperation("4.15-生成当前流程图表")
    @ApiOperationSupport(order = 15)
    @GetMapping(value = "processDiagram")
    public void genProcessDiagram(HttpServletResponse httpServletResponse,
                                  String processInstanceId) {
        String processDefinitionId = "";
        if (this.isFinished(processInstanceId)) {// 如果流程已经结束，则得到结束节点
            HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
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

    @ApiOperation("4.16-获取所有节点信息")
    @ApiOperationSupport(order = 16)
    @GetMapping(value = "getNodeList")
    public Result getNodeList(String processInstanceId) {
        List<HistoricActivityInstance> arr = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();
        String processDefinitionId = arr.get(0).getProcessDefinitionId();
        // 获取所有节点信息，暂不考虑子流程情况
        List<Process> processes = repositoryService.getBpmnModel(processDefinitionId).getProcesses();
        List<FlowElement> allNodes = new ArrayList<>();
        for (Process process : processes) {
            Collection<FlowElement> flowElements = process.getFlowElements();
            if (CollectionUtils.isNotEmpty(flowElements)) {
                for (FlowElement flowElement : flowElements) {
                    if (flowElement instanceof StartEvent) {
                        allNodes.add(flowElement);
                        //业务操作
                    } else if (flowElement instanceof UserTask) {
                        allNodes.add(flowElement);
                    } else if (flowElement instanceof StartEvent) {
                        allNodes.add(flowElement);
                    }
                    if (flowElement instanceof SubProcess) {
                        //，，，
                    }
                }
            }
        }
        return ResultUtil.data(allNodes);
    }

    @ApiOperation("4.17-获取所有用户任务节点信息")
    @ApiOperationSupport(order = 17)
    @GetMapping(value = "getTaskNodeList")
    public Result getTaskNodeList(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        // 获得所有流程定的节点信息
        List<UserTask> userTasks = flowableBpmnModelService.findUserTasksByBpmnModel(bpmnModel);
        return ResultUtil.data(userTasks);
    }

    public boolean isFinished(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().finished()
                .processInstanceId(processInstanceId).count() > 0;
    }

    public void claim(String taskId, String userId) {
        log.info("-----认领签收任务ID:{}，签收人ID:{}---------", taskId, userId);
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .active()
                .singleResult();
        if (task != null && StringUtils.isNotBlank(task.getAssignee())) {
            throw new BusinessRuntimeException("任务已经认领,不能重复认领!");
        }
        // 完成签收这个时候就任务Task的Assignee属性就有了值,
        // 已经签收过的任务列表（待办列表)
        taskService.claim(taskId, userId);
    }

    public void unClaim(String taskId) {
        log.info("-----反签收任务ID:{}---------", taskId);
        taskService.unclaim(taskId);
    }

    public Map<String, Object> complete(Task task, String userId, Map<String, Object> variables, boolean localScope) {

//        //修改执行人 其实我这里就相当于签收了
        taskService.setAssignee(task.getId(), userId);
        taskService.complete(task.getId(), variables, localScope);
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).active().list();
        Map<String, Object> map = new HashMap<>(16);
        Map<String, Object> finish = new HashMap<>(16);
        finish.put("id", task.getId());
        finish.put("name", task.getName());

        map.put("finish", finish);
        List<Map<String, Object>> taskList = new ArrayList<>();
        for (Task taskTmp : tasks) {
            Map<String, Object> taskMap = new HashMap<>(16);
            taskMap.put("id", taskTmp.getId());
            taskMap.put("name", taskTmp.getName());
            taskList.add(taskMap);
        }
        map.put("active", taskList);
        return map;
    }

    public void setAssignee(String taskId, String userId) {
        log.info("-----移交任务ID:{},移交给用户ID:{}---------", taskId, userId);
        taskService.setAssignee(taskId, userId);
    }

    public void delegate(String taskId, String userId) {
        log.info("-----委派任务ID:{},委派给用户ID:{}---------", taskId, userId);
        //是将任务节点分给其他人处理，等其他人处理好之后，委派任务会自动回到委派人的任务中
        taskService.delegateTask(taskId, userId);
    }

    public void resolve(String taskId) {
        log.info("-----委派完成任务ID:{}---------", taskId);
        // 被委派任务的办理: 办理完成后，委派任务会自动回到委派人的任务中
        taskService.resolveTask(taskId);
    }

    public void delete(String taskId) {
        log.info("-----删除任务：任务ID:{}---------", taskId);
        taskService.deleteTask(taskId);
    }

    public void signBackward(String taskId, String userId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String dynamicUserId = "dynamicUserTask-" + UUID.randomUUID().toString().replaceAll("-", "");
        DynamicUserTaskBuilder dynamicUserTaskBuilder = new DynamicUserTaskBuilder();
        dynamicUserTaskBuilder.setId(dynamicUserId);
        dynamicUserTaskBuilder.setName("临时向后加签");
        dynamicUserTaskBuilder.setAssignee(userId);

        ManagementService managementService = processEngine.getManagementService();

        //流程通过的el表达式变量,获取下一个UserTask。
        List<UserTask> nextUserTasks = null;
        try {
            nextUserTasks = FlowableUtils.getNextUserTasks(repositoryService, task);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (CollectionUtils.isNotEmpty(nextUserTasks)) {
            managementService.executeCommand(new AfterSignUserTaskCmd(task.getProcessInstanceId(),
                    dynamicUserTaskBuilder, task.getId(), nextUserTasks.get(0)));
        }
    }

    public void signForward(String taskId, String userId) {
        ManagementService managementService = processEngine.getManagementService();

        DynamicUserTaskBuilder taskBuilder = new DynamicUserTaskBuilder();
        taskBuilder.setName("临时向前加签");
        taskBuilder.setId("dynamicUserTask-" + UUID.randomUUID().toString().replaceAll("-", ""));
        taskBuilder.setAssignee(userId);

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        managementService.executeCommand(new BeforeSignUserTaskCmd(task.getProcessInstanceId(), taskBuilder, taskId));
        // managementService.executeCommand(new TaskJumpCmd(taskId, task.getId()));
    }

    public void track(String taskId, String userId) {
        taskService.addUserIdentityLink(taskId, userId, IdentityLinkType.PARTICIPANT);
    }

    public void read(String taskId, String userId) {
        managementService.executeCommand(new CompleteTaskReadCmd(taskId, userId));
    }

    public void press(String taskId, String userId, String callUrl) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Assert.isTrue(task != null, "taskId:[" + taskId + "]任务不存在");
        Comment comment = taskService.addComment(task.getId(), task.getProcessInstanceId(), ActionEnum.PRESS.getName());
        comment.setUserId(userId);
        //执行rest接口回调
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<Object> formEntity = new HttpEntity<>(callUrl, headers);
        String result = restTemplate.postForObject(callUrl, formEntity, String.class);
        taskService.saveComment(comment);
    }

    public void carbonCopy(Task task, String userid, String[] ccToVos) {
        managementService.executeCommand(new AddCcIdentityLinkCmd(task.getProcessInstanceId(), task.getId(), userid, ccToVos,identitylinkMapper));
    }

}
