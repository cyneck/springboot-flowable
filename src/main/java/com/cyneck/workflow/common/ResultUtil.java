package com.cyneck.workflow.common;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:02
 **/
public class ResultUtil<T> {
    private Result<T> result;

    public ResultUtil() {
        result = new Result<T>();
        result.setSuccess(true);
        //操作成功
        result.setMessage(ResultEnums.OK.getReasonPhraseCN());
        result.setCode(ResultEnums.OK.getCode());
    }

    public Result<T> setData(T t) {
        this.result.setResult(t);
        this.result.setCode(ResultEnums.OK.getCode());
        return this.result;
    }

    public Result<T> setSuccessMsg(String msg) {
        this.result.setSuccess(true);
        this.result.setMessage(msg);
        this.result.setCode(ResultEnums.OK.getCode());
        this.result.setResult(null);
        return this.result;
    }

    public Result<T> setData(T t, String msg) {
        this.result.setResult(t);
        this.result.setCode(ResultEnums.OK.getCode());
        this.result.setMessage(msg);
        return this.result;
    }

    public Result<T> setErrorMsg(String msg) {
        this.result.setSuccess(false);
        this.result.setMessage(msg);
        this.result.setCode(ResultEnums.FAIL.getCode());
        return this.result;
    }

    public Result<T> setErrorMsg(String code, String msg) {
        this.result.setSuccess(false);
        this.result.setMessage(msg);
        this.result.setCode(code);
        return this.result;
    }

    public Result<T> setErrorMsg(String code, String msg, T result) {
        this.result.setSuccess(false);
        this.result.setMessage(msg);
        this.result.setCode(code);
        this.result.setResult(result);
        return this.result;
    }

    public static <T> Result<T> data(T t) {
        return new ResultUtil<T>().setData(t);
    }

    public static <T> Result<T> data(T t, String msg) {
        return new ResultUtil<T>().setData(t, msg);
    }

    public static <T> Result<T> success() {
        return new ResultUtil<T>().setSuccessMsg("OK");
    }

    public static <T> Result<T> success(String msg) {
        return new ResultUtil<T>().setSuccessMsg(msg);
    }

    public static <T> Result<T> error(String msg) {
        return new ResultUtil<T>().setErrorMsg(msg);
    }

    public static <T> Result<T> error(String code, String msg) {
        return new ResultUtil<T>().setErrorMsg(code, msg);
    }

    public static <T> Result<T> error(String code, String msg, T result) {
        return new ResultUtil<T>().setErrorMsg(code, msg, result);
    }
}
