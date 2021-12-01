package com.cyneck.workflow.model.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>Description: 流程节点</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/22 19:39
 **/
@ApiModel(description = "流程节点")
@Data
public class FlowNodeResponse {
    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id", position = 1)
    private String nodeId;
    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称", position = 2)
    private String nodeName;
    /**
     * 执行人的id
     */
    @ApiModelProperty(value = "执行人的id", position = 3)
    private String userId;
    /**
     * 执行人姓名
     */
    @ApiModelProperty(value = "执行人姓名", position = 4)
    private String userName;

    /**
     * 任务节点结束时间
     */
    @ApiModelProperty(value = "任务节点结束时间", position = 5)
    private Date endTime;
}
