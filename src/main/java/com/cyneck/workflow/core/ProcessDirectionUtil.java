package com.cyneck.workflow.core;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Description: 判断流程方向</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/6/28 17:58
 **/

public class ProcessDirectionUtil {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    public void test(String processInstanceId){


    }

}
