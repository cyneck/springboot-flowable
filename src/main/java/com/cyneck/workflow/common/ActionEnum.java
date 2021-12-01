package com.cyneck.workflow.common;


import com.cyneck.workflow.annotation.SwaggerDisplayEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: 工作流任务类型</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:39
 **/
@Getter
@AllArgsConstructor
@SwaggerDisplayEnum(value = "action", name = "name")
public enum ActionEnum {

    COMPLETE("complete", "完成"),

    CLAIM("claim", "认领签收"),
    UNCLAIM("unclaim", "取消认领签收"),

    DELEGATE("delegate", "委派"),
    RESOLVE("resolve", " 完成委派"),

    ASSIGNEE("assignee", "转办"),

    SUSPEND("suspend", "挂起"),
    ACTIVATE("activate", "激活"),
    STOP("stop", "终止"),

    SIGN_BACKWARD("sign_backward", "向后加签"),
    SIGN_FORWARD("sign_forward", "向前加签"),

    TRACK("track", "跟踪"),

    CARBON_COPY("carbon_copy", "抄送"),

    READ("read", "已阅"),

    TAKE_BACK("take_back", "撤回"),

    PRESS("press", "催办");


    private String action;
    private String name;

    public static ActionEnum actionOf(String action) {
        for (ActionEnum actionEnum : values()) {
            if (actionEnum.getAction().equals(action.toLowerCase())) {
                return actionEnum;
            }
        }
        throw new RuntimeException("[任务类型]未找到对应的枚举");
    }

    @Override
    public String toString() {
        return String.valueOf(action) + ":" + name;
    }
}
