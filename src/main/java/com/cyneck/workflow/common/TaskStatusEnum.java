package com.cyneck.workflow.common;

/**
 * <p>Description: 任务状态枚举</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/1 16:58
 **/
public enum TaskStatusEnum {

    TODO(1, "待做"),

    DONE(2, "已完成");

    private Integer code;
    private String desc;

    TaskStatusEnum(Integer code, String desc) {
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
