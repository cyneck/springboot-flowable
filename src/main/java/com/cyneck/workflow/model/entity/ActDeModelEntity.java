package com.cyneck.workflow.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.cyneck.workflow.common.ModelType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Eric.Lee
 * @since 2021-01-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("act_de_model")
@ApiModel(value = "ActDeModelEntity", description = "模型定义实体")
public class ActDeModelEntity extends Model<ActDeModelEntity> implements Serializable {

    private static final long serialVersionUID = 4977531537706165288L;

    @ApiModelProperty(value = "模型id", position = 1)
    @TableId
    private String id;

    @ApiModelProperty(value = "模型名", position = 2)
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "模型类型（0:流程，2:表单，3:应用）", position = 3)
    @TableField("model_key")
    private String modelKey;

    @ApiModelProperty(value = "描述", position = 4)
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "备注", position = 5)
    @TableField("model_comment")
    private String modelComment;

    @ApiModelProperty(value = "创建时间", position = 6)
    @TableField("created")
    private LocalDateTime created;

    @ApiModelProperty(value = "创建人", position = 7)
    @TableField("created_by")
    private String createdBy;

    @ApiModelProperty(value = "修改时间", position = 8)
    @TableField("last_updated")
    private LocalDateTime lastUpdated;

    @ApiModelProperty(value = "修改人", position = 9)
    @TableField("last_updated_by")
    private String lastUpdatedBy;

    @ApiModelProperty(value = "版本", position = 10)
    @TableField("version")
    private Integer version;

    @ApiModelProperty(value = "模型编辑器需要的json字符串", position = 11)
    @TableField("model_editor_json")
    private String modelEditorJson;

//    // 字段数据意义不大
//    @TableField("thumbnail")
//    private Object thumbnail;

    /**
     * @see ModelType
     */
    @ApiModelProperty(value = "模型类型（0:流程，2:表单，3:应用）", position = 12)
    @TableField("model_type")
    private Integer modelType;

    @ApiModelProperty(value = "租户id", position = 13)
    @TableField("tenant_id")
    private String tenantId;

    @ApiModelProperty(value = "关联的子模型", position = 14)
    @TableField(exist = false)
    public List<ActDeModelEntity> children;


}
