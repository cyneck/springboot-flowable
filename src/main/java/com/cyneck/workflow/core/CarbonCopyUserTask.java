package com.cyneck.workflow.core;

import org.flowable.bpmn.model.UserTask;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: 抄送用户功能 </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/26 15:56
 **/
public class CarbonCopyUserTask extends UserTask {
    //抄送用户
    protected List<String> candidateNotifyUsers = new ArrayList();

    public List<String> getCandidateNotifyUsers() {
        return candidateNotifyUsers;
    }

    public void setCandidateNotifyUsers(List<String> candidateNotifyUsers) {
        this.candidateNotifyUsers = candidateNotifyUsers;
    }

    public CarbonCopyUserTask clone() {
        CarbonCopyUserTask clone = new CarbonCopyUserTask();
        clone.setValues(this);
        return clone;
    }

    public void setValues(CarbonCopyUserTask otherUserTask) {
        super.setValues(otherUserTask);
        this.setCandidateNotifyUsers(otherUserTask.getCandidateNotifyUsers());
    }
}
