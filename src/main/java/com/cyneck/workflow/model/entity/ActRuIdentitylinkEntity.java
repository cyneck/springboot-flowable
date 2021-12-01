package com.cyneck.workflow.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;

/**
 *
 * @author Eric.Lee
 * @since 2021-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("act_ru_identitylink")
public class ActRuIdentitylinkEntity implements Serializable {

	private static final long serialVersionUID = 7394178102488773708L;
	@TableId(type = IdType.AUTO)
 	@TableField("id")
	private String id;

 	@TableField("rev")
	private Integer rev;

 	@TableField("groupId")
	private String groupId;

 	@TableField("type")
	private String type;

 	@TableField("userId")
	private String userId;

 	@TableField("taskId")
	private String taskId;

 	@TableField("procInstId")
	private String procInstId;

 	@TableField("procDefId")
	private String procDefId;

 	@TableField("scopeId")
	private String scopeId;

 	@TableField("subScopeId")
	private String subScopeId;

 	@TableField("scopeType")
	private String scopeType;

 	@TableField("scopeDefinitionId")
	private String scopeDefinitionId;


}
