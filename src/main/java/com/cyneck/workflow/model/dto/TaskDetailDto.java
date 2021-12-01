package com.cyneck.workflow.model.dto;

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
public class TaskDetailDto extends HistoricTaskDto{

    private String delegationState;
    private boolean suspended;
    protected String ownerName;
    protected String assigneeName;
    private String formKey;

    public void setDelegationState(DelegationState delegationState) {
        this.delegationState = getDelegationStateString(delegationState);
    }

    private String getDelegationStateString(DelegationState state) {
        String result = null;
        if (state != null) {
            result = state.toString().toLowerCase();
        }
        return result;
    }

}
