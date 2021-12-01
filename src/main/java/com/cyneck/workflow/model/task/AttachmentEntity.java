package com.cyneck.workflow.model.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.flowable.common.engine.impl.persistence.entity.ByteArrayEntity;

import java.util.Date;

/**
 * <p>Description: 附件实体</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/22 18:42
 **/
@ApiModel(description = "流程附件实体")
@Data
public class AttachmentEntity {

    @ApiModelProperty(value = "附件id", position = 0)
    private String id;

    @ApiModelProperty(value = "附件名称", required = true, position = 2)
    private String name;

    @ApiModelProperty(value = "描述", position = 4)
    private String description;

    @ApiModelProperty(value = "附件类型", position = 6)
    private String type;

    @ApiModelProperty(value = "任务id", required = true, position = 8)
    private String taskId;

    @ApiModelProperty(value = "用户id", position = 9)
    private String userId;

    @ApiModelProperty(value = "流程实例id", required = true, position = 10)
    private String processInstanceId;

    @ApiModelProperty(value = "附件url", required = true, position = 12)
    private String url;

    @ApiModelProperty(value = "附件内容id", position = 14)
    private String contentId;

    @ApiModelProperty(value = "附件内容", position = 16)
    @JsonIgnore
    private ByteArrayEntity content;

    @ApiModelProperty(value = "创建时间", position = 20)
    private Date time;

    @ApiModelProperty(value = "id前缀", position = 22)
    private String idPrefix;

    @ApiModelProperty(value = "修订号", position = 24)
    @JsonIgnore
    private Integer revision;

    @ApiModelProperty(value = "下一个修订号", position = 26)
    @JsonIgnore
    private Integer revisionNext;

    @ApiModelProperty(value = "是否插入", position = 28)
    @JsonIgnore
    private boolean inserted;

    @ApiModelProperty(value = "是否删除", position = 30)
    @JsonIgnore
    private boolean deleted;

    @ApiModelProperty(value = "是否更新", position = 32)
    @JsonIgnore
    private boolean updated;
}
