package com.cyneck.workflow.model.entity;

import lombok.Data;

/**
 * 公司信息
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/18 12:35
 **/
@Data
public class MyCompanyInfo {
    private String id;
    private String alias;
    private String name;
    private String type;
    private String city;
    private String roleScore;
    private String roleName;
}
