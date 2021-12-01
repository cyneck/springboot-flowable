package com.cyneck.workflow.common;

import com.cyneck.workflow.annotation.SwaggerDisplayEnum;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/21 14:06
 **/
@SwaggerDisplayEnum(value = "code", name = "desc")
public enum ModelType {
    BPMN(0, "bpmn"),
    FORM(2, "form"),
    APP(3, "app"),
    DECISION_TABLE(4, "decision_table"),
    CMMN(5, "cmmn"),
    DECISION_SERVICE(6, "decision_service");


    private Integer code;
    private String desc;

    ModelType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


}
