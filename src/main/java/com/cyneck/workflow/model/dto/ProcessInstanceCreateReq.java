package com.cyneck.workflow.model.dto;

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

    @ApiModelProperty("流程定义ID")
    private String processDefinitionId;

    @ApiModelProperty("流程KEY(默认是最新版本的)")
    private String processDefinitionKey;

    @ApiModelProperty("业务主键")
    private String businessKey;

    @ApiModelProperty("用户Id")
    private String userId;

    @ApiModelProperty("租户ID")
    private String tenantId;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("业务名称")
    private String businessName;

    @ApiModelProperty("表单数据")
    private Map<String, Object> formData = new HashMap<>();

    public ProcessInstanceCreateReq() {
    }

    public ProcessInstanceCreateReq(String processDefinitionKey, String businessKey, String businessType, String businessName, Map<String, Object> var) {
        setProcessDefinitionKey(processDefinitionKey);
        setBusinessKey(businessKey);
        setBusinessType(businessType);
        setBusinessName(businessName);
        formData.putAll(var);
        formData.put(businessType, businessType);
        formData.put(businessName, businessName);
    }
}
