package com.cyneck.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyneck.workflow.model.entity.BCompanyInfoEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/20 23:37
 **/
public interface CustomizeUserGroupMapper extends BaseMapper<BCompanyInfoEntity> {

    @Select("SELECT c.id,\n" +
            "       c.alias,\n" +
            "       c.`name`,\n" +
            "       c.type,\n" +
            "       c.city,\n" +
            "       c.role_score    roleScore,\n" +
            "       CASE c.role_score\n" +
            "           WHEN 20 THEN '监管单位'\n" +
            "           WHEN 40 THEN '协同单位'\n" +
            "           WHEN 60 THEN '重点单位'\n" +
            "           WHEN 80 THEN '普通单位'\n" +
            "           else '' END roleName\n" +
            "FROM t_company c \n" +
            "WHERE c.id = #{companyId};\n")
    BCompanyInfoEntity findCompany(String companyId);

    @Select("SELECT c.id,\n" +
            "       c.alias,\n" +
            "       c.`name`,\n" +
            "       c.type,\n" +
            "       c.city,\n" +
            "       c.role_score    roleScore,\n" +
            "       CASE c.role_score\n" +
            "           WHEN 20 THEN '监管单位'\n" +
            "           WHEN 40 THEN '协同单位'\n" +
            "           WHEN 60 THEN '重点单位'\n" +
            "           WHEN 80 THEN '普通单位'\n" +
            "           else '' END roleName\n" +
            " FROM t_company c \n" +
            " where c.`name` like concat(#{name},'%') limit 10")
    List<BCompanyInfoEntity> findCompanys(String name);
}
