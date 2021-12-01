package com.cyneck.workflow.controller;

import com.cyneck.workflow.common.BusinessRuntimeException;
import com.cyneck.workflow.common.UserInfoConstant;
import com.cyneck.workflow.model.entity.BUserInfoEntity;
import com.cyneck.workflow.service.impl.UserCustomServiceCustomImpl;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.ManagementService;
import org.flowable.idm.api.IdmIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 公共接口
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/18 19:34
 **/
@RestController
public class ApiController {

    @Resource
    UserCustomServiceCustomImpl userService;

    @Autowired
    protected IdmIdentityService idmIdentityService;

    @Autowired
    protected ManagementService managementService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    HttpServletResponse httpServletResponse;

    public BUserInfoEntity getUserInfo() {
        BUserInfoEntity userInfo = null;
        String userId = httpServletRequest.getHeader(UserInfoConstant.USER_ID);
        if (StringUtils.isNotBlank(userId)) {
//        String companyId = request.getHeader(UserInfoConstant.COMPANY_ID);
            userInfo = userService.findUser(userId);
            if (userInfo == null) {
                throw new BusinessRuntimeException("用户不存在！");
            }
        }
        return userInfo;
    }
}
