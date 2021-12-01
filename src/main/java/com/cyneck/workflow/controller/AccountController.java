package com.cyneck.workflow.controller;


import com.cyneck.workflow.common.Result;
import com.cyneck.workflow.common.ResultUtil;
import com.cyneck.workflow.model.entity.BUserInfoEntity;
import com.cyneck.workflow.model.entity.MyUserInfo;
import com.cyneck.workflow.service.impl.UserCustomServiceCustomImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiSort;
import org.flowable.engine.ManagementService;
import org.flowable.idm.api.IdmIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: 用户控制器</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 10:31
 **/
@Api(tags = "5-用户信息接口", value = "AccountController")
@ApiSort(value = 5)
@RestController
@RequestMapping("/api/app")
public class AccountController {
    @Autowired
    UserCustomServiceCustomImpl userService;
    @Autowired
    protected IdmIdentityService idmIdentityService;
    @Autowired
    protected ManagementService managementService;

    @ApiOperation("5.1-查询用户信息")
    @GetMapping(value = "/rest/account", produces = "application/json")
    @ApiOperationSupport(order = 1)
    public Result getAccount(String userId) {
        BUserInfoEntity userInfo =userService.findUser(userId);
        return ResultUtil.data(userInfo);
    }

}
