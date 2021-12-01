package com.cyneck.workflow.common;

import com.cyneck.workflow.annotation.SwaggerDisplayEnum;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:02
 **/
@Getter
@SwaggerDisplayEnum(value = "code", name = "reasonPhraseCN")
public enum ResultEnums {
    /**
     * 功能描述: 状态码枚举所有状态码注解
     *
     * @author: Eric Lee
     * @date: 2020/7/16 19:09
     */
    OK("200", "OK", "操作成功"),
    FAIL("10001", "Operation Failure", "操作失败"),
    PARAMETER_ERROR("10002", "Parameter Error", "参数错误"),

    PASSWORD_RESET_SUCCESSFULLY("10003", "Password Reset Successfully", "重置密码成功"),
    USER_NO_LOGIN("20002", "User Is Not Logged In", "用户未登录"),
    LOGIN_PASSWORD_ERROR("20003", "Login Password Error", "登录密码错误"),
    OLD_PASSWORD_ERROR("20004", "Old Password Error", "旧密码错误"),
    PASSWORD_RESET_FAILED("20005", "Password Reset Failed", "重置密码失败"),
    USER_NAME_ALREADY_EXISTS("20006", "User Name Already Exists", "用户名已存在"),

    INTERNAL_SERVER_ERROR("500", "Internal Server Error", "服务器内部错误");

    String code; //状态码
    String reasonPhraseUS;//英文,首字母大写，中间一个空格隔开
    String reasonPhraseCN;//中文

    ResultEnums(String code, String reasonPhraseUS, String reasonPhraseCN) {
        this.code = code;
        this.reasonPhraseUS = reasonPhraseUS;
        this.reasonPhraseCN = reasonPhraseCN;
    }
}
