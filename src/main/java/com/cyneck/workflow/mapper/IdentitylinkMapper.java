package com.cyneck.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyneck.workflow.model.entity.ActRuIdentitylinkEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/3/2 18:44
 **/
public interface IdentitylinkMapper extends BaseMapper<ActRuIdentitylinkEntity> {


    @Select("select ID_                  id,\n" +
            "       REV_                 rev,\n" +
            "       GROUP_ID_            groupId,\n" +
            "       TYPE_                type,\n" +
            "       USER_ID_             userId,\n" +
            "       TASK_ID_             taskId,\n" +
            "       PROC_INST_ID_        procInstId,\n" +
            "       PROC_DEF_ID_         procDefId,\n" +
            "       SCOPE_ID_            scopeId,\n" +
            "       SUB_SCOPE_ID_        subScopeId,\n" +
            "       SCOPE_TYPE_          scopeType,\n" +
            "       SCOPE_DEFINITION_ID_ scopeDefinitionId\n" +
            "from act_ru_identitylink where USER_ID_=#{userId} and PROC_INST_ID_=#{processInstanceId} and type='CC'")
    List<ActRuIdentitylinkEntity> getListByProcInsId(@Param("userId") String userId,@Param("processInstanceId") String processInstanceId);

    @Select("select ID_                  id,\n" +
            "       REV_                 rev,\n" +
            "       GROUP_ID_            groupId,\n" +
            "       TYPE_                type,\n" +
            "       USER_ID_             userId,\n" +
            "       TASK_ID_             taskId,\n" +
            "       PROC_INST_ID_        procInstId,\n" +
            "       PROC_DEF_ID_         procDefId,\n" +
            "       SCOPE_ID_            scopeId,\n" +
            "       SUB_SCOPE_ID_        subScopeId,\n" +
            "       SCOPE_TYPE_          scopeType,\n" +
            "       SCOPE_DEFINITION_ID_ scopeDefinitionId\n" +
            "from act_ru_identitylink where USER_ID_=#{userId} and TASK_ID_ =#{taskId} and TYPE_='CC' limit 1")
    ActRuIdentitylinkEntity getCCUserLinkByUserTaskId(@Param("userId") String userId, @Param("taskId") String taskId);

    @Select("select ID_                  id,\n" +
            "       REV_                 rev,\n" +
            "       GROUP_ID_            groupId,\n" +
            "       TYPE_                type,\n" +
            "       USER_ID_             userId,\n" +
            "       TASK_ID_             taskId,\n" +
            "       PROC_INST_ID_        procInstId,\n" +
            "       PROC_DEF_ID_         procDefId,\n" +
            "       SCOPE_ID_            scopeId,\n" +
            "       SUB_SCOPE_ID_        subScopeId,\n" +
            "       SCOPE_TYPE_          scopeType,\n" +
            "       SCOPE_DEFINITION_ID_ scopeDefinitionId\n" +
            "from act_ru_identitylink where USER_ID_=#{userId} and TYPE_='CC' and TASK_ID_ is not null")
    List<ActRuIdentitylinkEntity> getUserTaskLinkListByUserId(@Param("userId") String userId);

    @Select("select ID_                  id,\n" +
            "       REV_                 rev,\n" +
            "       GROUP_ID_            groupId,\n" +
            "       TYPE_                type,\n" +
            "       USER_ID_             userId,\n" +
            "       TASK_ID_             taskId,\n" +
            "       PROC_INST_ID_        procInstId,\n" +
            "       PROC_DEF_ID_         procDefId,\n" +
            "       SCOPE_ID_            scopeId,\n" +
            "       SUB_SCOPE_ID_        subScopeId,\n" +
            "       SCOPE_TYPE_          scopeType,\n" +
            "       SCOPE_DEFINITION_ID_ scopeDefinitionId\n" +
            "from act_ru_identitylink where TYPE_=#{type} and USER_ID_ = #{userId} and TASK_ID_ is not null ")
    List<ActRuIdentitylinkEntity> getUserTaskListByType(@Param("type") String type, @Param("userId") String userId);
}
