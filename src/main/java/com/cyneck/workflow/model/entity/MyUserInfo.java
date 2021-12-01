package com.cyneck.workflow.model.entity;

import lombok.Data;

/**
 * 业务用户信息
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/18 11:32
 **/
@Data
public class MyUserInfo {

    private String userId;
    private String userName;
    private String realName;
    private String email;
    private String roleId;
    private String roleName;
    private String companyId;
    private String companyName;
    private String companyCity;
    private String companyType;

}
