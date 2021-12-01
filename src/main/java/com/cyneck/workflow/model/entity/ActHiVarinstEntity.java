package com.cyneck.workflow.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author Eric.Lee
 * @since 2021-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("act_hi_varinst")
public class ActHiVarinstEntity implements Serializable {

	private static final long serialVersionUID = 1398412292419947686L;
	@TableId(type = IdType.AUTO)
 	@TableField("id")
	private String id;

 	@TableField("rev")
	private Integer rev;

 	@TableField("procInstId")
	private String procInstId;

 	@TableField("executionId")
	private String executionId;

 	@TableField("taskId")
	private String taskId;

 	@TableField("name")
	private String name;

 	@TableField("type")
	private String type;

 	@TableField("scopeId")
	private String scopeId;

 	@TableField("subScopeId")
	private String subScopeId;

 	@TableField("scopeType")
	private String scopeType;

 	@TableField("bytearrayId")
	private String bytearrayId;

 	@TableField("doubleI")
	private BigDecimal doubleI;

 	@TableField("longI")
	private Long longI;

 	@TableField("text")
	private String text;

 	@TableField("text2")
	private String text2;

 	@TableField("createTime")
	private LocalDateTime createTime;

 	@TableField("lastUpdatedTime")
	private LocalDateTime lastUpdatedTime;


}
