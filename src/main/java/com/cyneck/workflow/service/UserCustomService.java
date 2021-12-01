package com.cyneck.workflow.service;

import com.cyneck.workflow.model.entity.BCompanyInfoEntity;
import com.cyneck.workflow.model.entity.BUserInfoEntity;
import com.cyneck.workflow.model.entity.MyCompanyInfo;
import com.cyneck.workflow.model.entity.MyUserInfo;

import java.util.List;

/**
 * <p>Description: 用户服务</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:12
 **/
public interface UserCustomService {

    BUserInfoEntity findUser(String userId);

    BCompanyInfoEntity findGroup(String groupId);

    List<BUserInfoEntity> findUsers(String filter);

    List<BCompanyInfoEntity> findGroups(String name);
}
