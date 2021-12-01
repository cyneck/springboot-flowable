package com.cyneck.workflow.model.task;

import com.cyneck.workflow.model.dto.HistoricTaskDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.flowable.task.api.DelegationState;

/**
 * <p>Description: 任务明细 </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/1 17:07
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "TaskDetailDto", description = "历史任务详细Dto")
public class TaskDetailDto extends HistoricTaskDto {

    @ApiModelProperty(value = "所有者的名字", position = 1)
    protected String owner;

    @ApiModelProperty(value = "处理人的名字", position = 1)
    protected String assignee;

    @ApiModelProperty(value = "表单key", position = 1)
    private String formKey;

    @ApiModelProperty(value = "委托状态（PENDING：挂起， RESOLVED：已完成委托）", position = 1)
    private String delegationState;

    @ApiModelProperty(value = "是否挂起", position = 1)
    private boolean suspended;

    public void setDelegationState(DelegationState delegationState) {
        String result = null;
        if (delegationState != null) {
            result = delegationState.toString().toLowerCase();
        }
        this.delegationState = result;
    }

}
