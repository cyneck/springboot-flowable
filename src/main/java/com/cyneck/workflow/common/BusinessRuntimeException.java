package com.cyneck.workflow.common;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:04
 **/
@Data
public class BusinessRuntimeException extends RuntimeException {
    private String msg;

    public BusinessRuntimeException(String msg){
        super(msg);
        this.msg = msg;
    }
}
