package com.cyneck.workflow.listener;


import org.flowable.engine.impl.el.FixedValue;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * <p>Description:用户任务监听处理,rest接口回调 </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/22 19:13
 **/
@Component("userTaskListener")
public class UserTaskListener implements TaskListener {

    private FixedValue restUrl;

    @Override
    public void notify(DelegateTask delegateTask) {
        String processInstanceId = delegateTask.getProcessInstanceId();
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
        HttpEntity<Object> formEntity = new HttpEntity<>(delegateTask, headers);
        String result = restTemplate.postForObject(restUrlStr, formEntity, String.class);
    }

}
