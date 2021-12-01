package com.cyneck.workflow.listener;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.impl.el.FixedValue;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description: 任务执行监听</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/17 11:26
 **/

public class ExecutionBusinessCallListener implements ExecutionListener {
    /**
     * rest接口
     */
    private FixedValue restUrl;

    @Override
    public void notify(DelegateExecution delegateExecution) {
        ExecutionEntityImpl executionEntity = (ExecutionEntityImpl) delegateExecution;
        String processInstanceId = executionEntity.getProcessInstanceId();
        String processDefinitionId = executionEntity.getProcessDefinitionId();
        String processDefinitionKey = executionEntity.getProcessDefinitionKey();
        String processDefinitionName = executionEntity.getProcessDefinitionName();
        String deploymentId = executionEntity.getDeploymentId();
        String startUserId = executionEntity.getStartUserId();
        String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
        String tenantId = executionEntity.getTenantId();
        FlowElement currentFlowElement = executionEntity.getCurrentFlowElement();
        String businessKey = executionEntity.getBusinessKey();
        String currentActivityName = executionEntity.getCurrentActivityName();
        String activityName = executionEntity.getActivityName();
        String eventName = executionEntity.getEventName();
        String name = executionEntity.getName();
        String id = executionEntity.getId();
        String description = executionEntity.getDescription();
        int revision = executionEntity.getRevision();
        Date startTime = executionEntity.getStartTime();
        Map<String, Object> variables = delegateExecution.getVariables();

        Map map = new HashMap<String, Object>();
        map.put("processInstanceId", processInstanceId);
        map.put("processDefinitionId", processDefinitionId);
        map.put("processDefinitionKey", processDefinitionKey);
        map.put("processDefinitionName", processDefinitionName);
        map.put("deploymentId", deploymentId);
        map.put("startUserId", startUserId);
        map.put("processInstanceBusinessKey", processInstanceBusinessKey);
        map.put("tenantId", tenantId);
        map.put("currentFlowElement", currentFlowElement);
        map.put("businessKey", businessKey);
        map.put("currentActivityName", currentActivityName);
        map.put("activityName", activityName);
        map.put("eventName", eventName);
        map.put("name", name);
        map.put("id", id);
        map.put("description", description);
        map.put("revision", revision);
        map.put("startTime", startTime);

        String restUrlStr = "http://localhost:8080/workflow/call";
        if (restUrl != null) {
            restUrlStr = restUrl.getExpressionText();
        }
        //执行rest接口回调
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<Object> formEntity = new HttpEntity<>(map, headers);
        String result = restTemplate.postForObject(restUrlStr, formEntity, String.class);

    }

}
