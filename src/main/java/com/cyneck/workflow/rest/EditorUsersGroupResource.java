package com.cyneck.workflow.rest;

import com.cyneck.workflow.model.entity.BCompanyInfoEntity;
import com.cyneck.workflow.model.entity.BUserInfoEntity;
import com.cyneck.workflow.service.impl.UserCustomServiceCustomImpl;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import org.flowable.engine.ManagementService;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.ui.common.model.GroupRepresentation;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.common.model.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/20 19:24
 **/
@Api(tags = "modeler用户资源编辑接口", value = "EditorUsersGroupResource")
@ApiIgnore()
@RestController
@RequestMapping("/modeler/")
public class EditorUsersGroupResource {

    @Autowired
    UserCustomServiceCustomImpl userService;
    @Autowired
    protected IdmIdentityService idmIdentityService;
    @Autowired
    protected ManagementService managementService;

    @GetMapping(value = "rest/editor-users")
    public ResultListDataRepresentation getUsers(@RequestParam(value = "filter", required = false) String filter) {
        List<BUserInfoEntity> users = userService.findUsers(filter);
        List<UserRepresentation> userRepresentations = new ArrayList();
        for (BUserInfoEntity user : users) {
            String real_name = user.getRealName();
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setId(user.getUserId());
            userRepresentation.setFullName(real_name);
            //姓，名
            userRepresentation.setFirstName("");
            userRepresentation.setLastName(real_name);
            userRepresentation.setEmail(user.getEmail());

            List<GroupRepresentation> groups = Lists.newArrayList();
            GroupRepresentation groupRepresentation = new GroupRepresentation();
            groupRepresentation.setId(user.getCompanyId());
            groupRepresentation.setName(user.getCompanyName());
            groupRepresentation.setType(user.getCompanyType());
            groups.add(groupRepresentation);

            userRepresentation.setGroups(groups);
            userRepresentations.add(userRepresentation);
        }
        return new ResultListDataRepresentation(userRepresentations);
    }


    @GetMapping(value = "/rest/editor-groups")
    public ResultListDataRepresentation getGroups(@RequestParam(required = false, value = "filter") String filter) {
        List<BCompanyInfoEntity> roles = userService.findGroups(filter);
        List<GroupRepresentation> result = new ArrayList();
        for (BCompanyInfoEntity group : roles) {
            GroupRepresentation groupRepresentation = new GroupRepresentation();
            groupRepresentation.setId(group.getId());
            groupRepresentation.setName(group.getName());
            groupRepresentation.setType(group.getType());
            result.add(groupRepresentation);
        }
        return new ResultListDataRepresentation(result);
    }

}
