package com.cyneck.workflow.controller;

import cn.hutool.core.bean.BeanUtil;
import com.cyneck.workflow.common.ModelType;
import com.cyneck.workflow.common.Result;
import com.cyneck.workflow.common.ResultUtil;
import com.cyneck.workflow.model.dto.ModelDto;
import com.cyneck.workflow.model.entity.ActDeModelEntity;
import com.cyneck.workflow.service.CustomizeModelService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.ui.common.service.exception.BadRequestException;
import org.flowable.ui.common.service.exception.BaseModelerRestException;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.service.FlowableModelQueryService;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: 流程模型设计接口</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:40
 **/
@Api(tags = "1-模型接口", value = "ModelerController", description = "表单、流程、app等模型接口")
@ApiSort(value = 1)
@Transactional
@Slf4j
@RestController
@RequestMapping("/model/")
public class ModelerController extends ApiController {
    @Autowired
    RepositoryService repositoryService;

    @Autowired
    protected FlowableModelQueryService modelQueryService;

    @Autowired
    protected ModelService modelService;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private CustomizeModelService customizeModelService;


    @ApiOperation(value = "1.1-模型发布")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型id", required = true, dataType = "String")
    })
    @RequestMapping(value = "deploy", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<ModelDto> deploy(@RequestParam("modelId") String modelId) throws Exception {
        //查询模型
        Model modelData = modelService.getModel(modelId);
        byte[] bytes = modelService.getBpmnXML(modelData);
        if (bytes == null) {
            return ResultUtil.error("模型数据为空，请先设计流程并成功保存，再进行发布。");
        }
        BpmnModel model = modelService.getBpmnModel(modelData);
        if (model.getProcesses().size() == 0) {
            return ResultUtil.error("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = modelService.getBpmnXML(modelData);
        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = null;
        try {
            deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .addString(processName, new String(bpmnBytes, "UTF-8"))
                    .deploy();
        } catch (FlowableException e) {
            return ResultUtil.error("部署Flowable异常，异常信息：" + e);
        }
        Model newModelResult = modelService.saveModel(modelData);
        ModelDto result = new ModelDto();
        BeanUtil.copyProperties(newModelResult, result);
        return ResultUtil.data(result);
    }

    @ApiOperation(value = "1.2-查询所有流程模型")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelType", value = "模型类型（0:流程，2:表单，3:应用）", required = false, dataType = "Integer")
    })
    @GetMapping(value = "rest/models", produces = "application/json")
    public Result<List<ModelDto>> getModels(@RequestParam(required = false) Integer modelType) {
        List<Model> models = modelRepository.findByModelType(modelType, "");
        List<ModelDto> modelDtoList = new ArrayList<>();
        models.forEach(s -> {
            ModelDto modelDto = new ModelDto(s);
            modelDtoList.add(modelDto);
        });
        return ResultUtil.data(modelDtoList);
    }

    @ApiOperation(value = "1.3-查询模型相关联的模型")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelKey", value = "模型key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "modelType", value = "模型类型（0:流程，2:表单，3:应用）", required = true, dataType = "Integer")
    })
    @GetMapping(value = "rest/model", produces = "application/json")
    public Result<ActDeModelEntity> getAppModel(@RequestParam(required = true) String modelKey,
                                                @RequestParam(required = true) Integer modelType) {
        ActDeModelEntity modelEntity = customizeModelService.getModelAndRelationByKey(modelKey, modelType);
        return ResultUtil.data(modelEntity);
    }

    // 查看流程部署图片【只是一个图片，不会显示执行到那个节点】
    // procDefId：流程定义ID;proInsId ：流程实例ID;  resType：资源类型(xml|image)
    @ApiOperation(value = "1.4-流程模型打印(图片的字节流数据)", consumes = "")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "流程定义key", required = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "图片的字节流数据", response = byte[].class)
    })
    @GetMapping(value = "resourceRead")
    public void resourceRead(@RequestParam(required = true) String processDefinitionKey,
                             HttpServletResponse response) throws Exception {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
        String resourceName = processDefinition.getDiagramResourceName();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    /*
     * deploymentId:流程定义ID
     * 摘抄源码：org.flowable.ui.modeler.rest.app -> /rest/models/{processModelId}/bpmn20
     * */
    @ApiOperation(value = "1.5-模型xml下载")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "模型key", required = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "文件字节流数据", response = byte[].class)
    })
    @GetMapping(value = "resource/downloadXml")
    public void downloadXml(HttpServletResponse response,
                            HttpServletRequest request,
                            @RequestParam(name = "processDefinitionKey") String processDefinitionKey) {
        log.info("》》》》下载执行了》》》》");
        String processModelKey = processDefinitionKey;
        if (processModelKey == null) {
            throw new BadRequestException("No process model id provided");
        }
        ActDeModelEntity modelEntity = customizeModelService.getModelEntityByKey(processModelKey, ModelType.BPMN.getCode());
        Model model = modelService.getModel(modelEntity.getId());
        String name = model.getName().replaceAll(" ", "_") + ".bpmn20.xml";
        String encodedName = null;
        try {
            encodedName = "UTF-8''" + URLEncoder.encode(name, "UTF-8");
        } catch (Exception e) {
            log.warn("编码名称失败 " + name);
        }
        String contentDispositionValue = "attachment; filename=" + name;
        if (encodedName != null) {
            contentDispositionValue += "; filename*=" + encodedName;
        }
        response.setHeader("Content-Disposition", contentDispositionValue);
        if (model.getModelEditorJson() != null) {
            try {
                ServletOutputStream servletOutputStream = response.getOutputStream();
                response.setContentType("application/xml");
                BpmnModel bpmnModel = modelService.getBpmnModel(model);
                byte[] xmlBytes = modelService.getBpmnXML(bpmnModel);
                BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(xmlBytes));

                byte[] buffer = new byte[8096];
                while (true) {
                    int count = in.read(buffer);
                    if (count == -1) {
                        break;
                    }
                    servletOutputStream.write(buffer, 0, count);
                }
                // Flush and close stream
                servletOutputStream.flush();
                servletOutputStream.close();
            } catch (BaseModelerRestException e) {
                throw e;
            } catch (Exception e) {
                log.error("Could not generate BPMN 2.0 XML", e);
                throw new InternalServerErrorException("Could not generate BPMN 2.0 xml");
            }
        }
    }
}
