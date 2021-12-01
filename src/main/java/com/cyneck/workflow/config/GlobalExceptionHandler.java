package com.cyneck.workflow.config;

import com.cyneck.workflow.common.BusinessRuntimeException;
import com.cyneck.workflow.common.Result;
import com.cyneck.workflow.common.ResultEnums;
import com.cyneck.workflow.common.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/21 10:33
 **/
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    protected Result exp(HttpServletRequest request, Exception e) {
        log.error(e.getMessage(), e);
        return ResultUtil.error(ResultEnums.INTERNAL_SERVER_ERROR.getCode(), ResultEnums.INTERNAL_SERVER_ERROR.getReasonPhraseCN(), e.getMessage());
    }

    @ExceptionHandler(value = BusinessRuntimeException.class)
    protected Result exp(HttpServletRequest request, BusinessRuntimeException e) {
        log.error(e.getMessage(), e);
        return ResultUtil.error(ResultEnums.FAIL.getCode(), e.getMsg());
    }
}
