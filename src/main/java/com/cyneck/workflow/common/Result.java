package com.cyneck.workflow.common;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:01
 **/
@Data
@ApiModel(value = "统一返回数据格式", description = "统一返回数据格式")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    @ApiModelProperty(value = "是否成功", position = 1)
    private boolean success;

    /**
     * 消息
     */
    @ApiModelProperty(value = "消息", position = 2)
    private String message;

    /**
     * 返回代码
     */
    @ApiModelProperty(value = "返回代码", position = 3)
    private String code;

    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳", position = 4)
    private long timestamp = System.currentTimeMillis();
//    private long timestamp = SystemClock.now();

    /**
     * 结果对象
     */
    @ApiModelProperty(value = "结果对象", position = 5)
    private T result;

    public String toJsonString() {
        return JSONUtil.toJsonStr(this);
    }
}

