package com.cyneck.workflow.model.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.flowable.common.engine.impl.persistence.entity.ByteArrayEntity;

/**
 * 附件上传保存请求
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/25 14:02
 **/
@ApiModel(description = "流程附件实体")
@Data
public class AttachmentReq {

    @ApiModelProperty(value = "附件名称", required = true, position = 2)
    private String name;

    @ApiModelProperty(value = "描述", position = 4)
    private String description;

    @ApiModelProperty(value = "附件类型", position = 6)
    private String type;

    @ApiModelProperty(value = "任务id", required = true, position = 8)
    private String taskId;

    @ApiModelProperty(value = "附件url", required = true, position = 12)
    private String url;

    @ApiModelProperty(value = "附件内容", position = 16)
    @JsonIgnore
    private ByteArrayEntity content;

}
