package com.cyneck.workflow.model.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description: 流程创建请求实体</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:21
 **/
@ApiModel(value = "ProcessInstanceCreateRequest", description = "流程实例创建对象req")
@Data
public class ProcessInstanceCreateReq {

    @ApiModelProperty(value = "流程定义ID", position = 1, required = true)
    private String processDefinitionId;

//    @ApiModelProperty("流程KEY(默认是最新版本的)")
//    private String processDefinitionKey;

    @ApiModelProperty(value = "业务主键", position = 2)
    private String businessKey;

    @ApiModelProperty(value = "业务类型", position = 3)
    private String businessType;

    @ApiModelProperty(value = "业务名称", position = 4)
    private String businessName;

    @ApiModelProperty(value = "租户ID", position = 5)
    private String tenantId;

    @ApiModelProperty(value = "表单数据", position = 6)
    private Map<String, Object> formData = new HashMap<>();

    public ProcessInstanceCreateReq() {
    }

    public ProcessInstanceCreateReq(String processDefinitionKey, String businessKey, String businessType, String businessName, Map<String, Object> var) {
//        setProcessDefinitionKey(processDefinitionKey);
        setBusinessKey(businessKey);
        setBusinessType(businessType);
        setBusinessName(businessName);
        formData.putAll(var);
        formData.put(businessType, businessType);
        formData.put(businessName, businessName);
    }
}
