package com.cyneck.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyneck.workflow.model.entity.BUserInfoEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 自定义用户信息mapper
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/18 16:43
 **/
public interface CumstomizeUserMapper extends BaseMapper<BUserInfoEntity> {

    @Select("SELECT u.id       userId,\n" +
            "       u.name     userName,\n" +
            "       u.realname realName,\n" +
            "       u.email,\n" +
            "       r.id       roleId,\n" +
            "       r.name     roleName,\n" +
            "       u.company_id,\n" +
            "       c.`name`   companyName,\n" +
            "       c.city     companyCity,\n" +
            "       c.type     companyType\n" +
            "FROM t_user u\n" +
            "         left JOIN t_user_role ur ON u.id = ur.`user`\n" +
            "         left JOIN t_role r ON r.id = ur.role\n" +
            "         left join t_company c on u.company_id = c.company_id\n" +
            "where u.id = #{userId};")
    BUserInfoEntity findUser(String userId);


    @Select("SELECT\n" +
            "u.id userId,\n" +
            "u.name userName,\n" +
            "u.realname realName,\n" +
            "u.email,\n" +
            "r.id roleId,\n" +
            "r.name roleName,\n" +
            "u.company_id companyId,\n" +
            "c.`name` companyName,\n" +
            "c.city companyCity,\n" +
            "c.type companyType\n" +
            "FROM\n" +
            "t_user u\n" +
            "left JOIN t_user_role ur ON u.id = ur.`user`\n" +
            "left JOIN t_role r ON r.id = ur.role \n" +
            "left join t_company c on u.company_id=c.company_id " +
            "where u.realname like concat(#{realName},'%') limit 10")
    List<BUserInfoEntity> findUsers(String realName);
}
