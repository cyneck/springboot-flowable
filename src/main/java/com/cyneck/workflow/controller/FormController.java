package com.cyneck.workflow.controller;

import cn.hutool.core.bean.BeanUtil;
import com.cyneck.workflow.common.BusinessRuntimeException;
import com.cyneck.workflow.common.ModelType;
import com.cyneck.workflow.common.Result;
import com.cyneck.workflow.common.ResultUtil;
import com.cyneck.workflow.mapper.HiVarInstEntityMapper;
import com.cyneck.workflow.mapper.RuVariableMapper;
import com.cyneck.workflow.model.entity.ActDeModelEntity;
import com.cyneck.workflow.model.entity.ActHiVarinstEntity;
import com.cyneck.workflow.model.entity.ActRuVariableEntity;
import com.cyneck.workflow.model.form.*;
import com.cyneck.workflow.model.task.TaskResponse;
import com.cyneck.workflow.service.impl.CustomizeModelServiceImpl;
import com.cyneck.workflow.model.form.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.api.FormDeployment;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.task.api.Task;
import org.flowable.ui.common.service.exception.ConflictingRequestException;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelKeyRepresentation;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Description: ???????????????</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/13 14:30
 **/
@Api(tags = "2-????????????", value = "FormController")
@ApiSort(value = 2)
@Transactional
@Slf4j
@RestController
@RequestMapping("/form/")
public class FormController extends ApiController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private FormService formService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    protected ModelService modelService;

    @Autowired
    private FormRepositoryService formRepositoryService;

    @Autowired
    private CustomizeModelServiceImpl customizeModelService;

    @Resource
    RuVariableMapper taskVariableMapper;

    @Autowired
    private HistoryService historyService;

    @Autowired
    HiVarInstEntityMapper hiVarInstEntityMapper;


    /**
     * ?????????
     *
     * @param formModelRepresentationDto
     * @return
     */
    @ApiOperation(value = "2.1-?????????????????????(??????)")
    @ApiOperationSupport(order = 1)
    @ApiIgnore
    @PostMapping(value = "rest/create", produces = "application/json")
    public Result<FormModelRepresentationDto> createModel(@RequestBody FormModelRepresentationDto formModelRepresentationDto) {
        formModelRepresentationDto.setKey(formModelRepresentationDto.getKey().replaceAll(" ", ""));
        ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(null, formModelRepresentationDto.getModelType(), formModelRepresentationDto.getKey());
        if (modelKeyInfo.isKeyAlreadyExists()) {
            throw new ConflictingRequestException("?????????KEY????????????: " + formModelRepresentationDto.getKey());
        }
        ModelRepresentation modelRepresentation = new ModelRepresentation();
        BeanUtil.copyProperties(formModelRepresentationDto, modelRepresentation);
        String json = modelService.createModelJson(modelRepresentation);
        Model newModel = modelService.createModel(modelRepresentation, json, getUserInfo().getUserId());
        BeanUtil.copyProperties(newModel, formModelRepresentationDto);
        return ResultUtil.data(formModelRepresentationDto);
    }


    @ApiOperation(value = "2.1-?????????????????????????????????", notes = "????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????")
    @ApiOperationSupport(order = 1)
    @PostMapping(value = "saveFormData")
    public Result<String> saveFormData(@RequestBody() FormDataDto formDataDto) {
        String userID = getUserInfo().getUserId();
        Authentication.setAuthenticatedUserId(userID);
        formService.saveFormData(formDataDto.getTaskId(), formDataDto.getFormData());
        Authentication.setAuthenticatedUserId(null);
        return ResultUtil.success();
    }

    /**
     * ????????????
     *
     * @param completeFormData
     * @return
     */
    @ApiOperation(value = "2.3-????????????(?????????)", notes = "?????????????????????????????????????????????????????????")
    @ApiOperationSupport(order = 3)
    @ApiIgnore
    @PostMapping(value = "completeTaskForm")
    public Result<String> completeTaskForm(@RequestBody TaskCompleteFormData completeFormData) {
        String userID = getUserInfo().getUserId();
        Authentication.setAuthenticatedUserId(userID);
        taskService.completeTaskWithForm(completeFormData.getTaskId(),
                completeFormData.getFormDefinitionId(),
                "",
                completeFormData.getFormData());
        Authentication.setAuthenticatedUserId(null);
        return ResultUtil.success();
    }


    @ApiOperation(value = "2.2-????????????????????????????????????", notes = "????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstanceId", value = "????????????id", required = true, dataType = "String")
    })
    @GetMapping(value = "getProcessStartTaskFormData")
    public Result<FormDataVar> getProcessStartTaskFormData(@RequestParam String processInstanceId) {
        FormDataVar formDataVar = new FormDataVar();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (pi != null) {
            formDataVar.setProcessInstanceId(processInstanceId);
            Map<String, String> map = new HashMap<>();
            List<ActRuVariableEntity> ruVariableEntityList = taskVariableMapper.findListByProcInstId(processInstanceId);
            ruVariableEntityList.stream().forEach(s -> {
                if ("boolean".equals(s.getType())) {
                    map.put(s.getName(), s.getLongI() == 1 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
                } else {
                    map.put(s.getName(), s.getText());
                }
            });
            formDataVar.setFormData(map);
        } else {
            //??????????????????
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            if (historicProcessInstance == null) {
                throw new BusinessRuntimeException("?????????????????????");
            }
            formDataVar.setProcessInstanceId(processInstanceId);
            Map<String, String> map = new HashMap<>();
            List<ActHiVarinstEntity> hiVarinstEntityList=hiVarInstEntityMapper.findListByProcInstId(processInstanceId);
            hiVarinstEntityList.stream().forEach(s -> {
                if ("boolean".equals(s.getType())) {
                    map.put(s.getName(), s.getLongI() == 1 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
                } else {
                    map.put(s.getName(), s.getText());
                }
            });
            formDataVar.setFormData(map);
        }
        return ResultUtil.data(formDataVar);
    }

    @ApiOperation(value = "2.3-???????????????????????????", notes = "????????????????????????????????????????????????????????????")
    @ApiOperationSupport(order = 5)
    @ApiIgnore
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionId", value = "????????????id", required = true, dataType = "String")
    })
    @GetMapping(value = "deploymentFormModel")
    public Result<FormDefinition> deploymentFormModel(@RequestParam String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .active()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        StartFormData form = formService.getStartFormData(processDefinition.getId());
        ActDeModelEntity modelEntity = customizeModelService.getModelAndRelationByKey(form.getFormKey(), ModelType.FORM.getCode());
        FormDeployment formDeployment = formRepositoryService.createDeployment()
                .name(form.getFormKey())
                .addString("form-" + form.getFormKey() + ".form", modelEntity.getModelEditorJson())
                .parentDeploymentId(processDefinition.getDeploymentId())
                .deploy();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().deploymentId(formDeployment.getId()).singleResult();
        return ResultUtil.data(formDefinition);
    }

    /**
     * ????????????
     *
     * @param taskId
     * @return
     */
    @ApiOperation("2.5-????????????id????????????????????????????????????????????????")
    @ApiOperationSupport(order = 5)
    @ApiIgnore
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "??????id", required = true, dataType = "String")
    })
    @GetMapping(value = "getProcessStartTaskFormDataVar")
    public Result<Map<String, Object>> getProcessStartTaskFormDataVar(@RequestParam String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Assert.isTrue(task != null, "???????????????");
        //??????????????????
        Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());
        //??????????????????
        Map<String, Object> map = taskService.getVariables(taskId);
        return ResultUtil.data(variables);
    }


    @ApiOperation(value = "2.3-??????????????????????????????", notes = "???????????????????????????????????????????????????????????????????????????")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "????????????key", required = true, dataType = "String"),
    })
    @GetMapping(value = "getStartFormModel")
    public Result<FormModel> getStartFormModel(@RequestParam String processDefinitionKey) {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
        if (null == pd) {
            throw new BusinessRuntimeException("???????????????????????????");
        }
        StartFormData form = formService.getStartFormData(pd.getId());
        ActDeModelEntity modelEntity = customizeModelService.getModelAndRelationByKey(form.getFormKey(), ModelType.FORM.getCode());
        ObjectMapper mapper = new ObjectMapper();
        FormModel formModel = new FormModel();
        try {
            Object obj = mapper.readValue(modelEntity.getModelEditorJson(), Object.class);
            BeanUtil.copyProperties(obj, formModel);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return ResultUtil.data(formModel);
    }


    @ApiOperation("2.7-??????????????????????????????")
    @ApiOperationSupport(order = 7)
    @ApiIgnore
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "????????????key", required = true, dataType = "String"),
    })
    @PostMapping(value = "getFormModel")
    public Result<List<Model>> getFormModel(@RequestParam String processDefinitionKey) {
        List<Model> models = modelRepository.findByKeyAndType(processDefinitionKey, ModelType.FORM.getCode());
        // todo:????????????
        return ResultUtil.data(models);
    }

    @ApiOperation("2.8-?????????????????????????????????(?????????)")
    @ApiOperationSupport(order = 8)
    @ApiIgnore
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "??????id", required = true, dataType = "String"),
    })
    @PostMapping(value = "getTaskForm")
    public Result<FormDataDetailDto> getTaskForm(@RequestParam String taskId) {
        TaskFormData form = formService.getTaskFormData(taskId);
        FormDataDetailDto formDataDto = new FormDataDetailDto();
        TaskResponse task = new TaskResponse();
        BeanUtil.copyProperties(form.getTask(), task);
        formDataDto.setTask(task);
        formDataDto.setDeploymentId(form.getDeploymentId());
        //TODO: ????????????
//        formDataDto.setFormData();
        return ResultUtil.data(formDataDto);
    }

}
