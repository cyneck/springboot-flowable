package com.cyneck.workflow.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cyneck.workflow.mapper.CumstomizeUserMapper;
import com.cyneck.workflow.mapper.CustomizeUserGroupMapper;
import com.cyneck.workflow.model.entity.BCompanyInfoEntity;
import com.cyneck.workflow.model.entity.BUserInfoEntity;
import com.cyneck.workflow.service.UserCustomService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:12
 **/

@Service
public class UserCustomServiceCustomImpl implements UserCustomService {

    @Resource
    CustomizeUserGroupMapper userGroupCustomMapper;

    @Resource
    CumstomizeUserMapper cumstomizeUserMapper;

    @DS("secondary")
    @Override
    public BUserInfoEntity findUser(String userId) {
        return cumstomizeUserMapper.findUser(userId);
    }

    @DS("secondary")
    @Override
    public List<BUserInfoEntity> findUsers(String filter) {
        return cumstomizeUserMapper.findUsers(filter);
    }

    @DS("secondary")
    @Override
    public BCompanyInfoEntity findGroup(String groupId) {
        return userGroupCustomMapper.findCompany(groupId);
    }

    @DS("secondary")
    @Override
    public List<BCompanyInfoEntity> findGroups(String name) {
        return userGroupCustomMapper.findCompanys(name);
    }


}
