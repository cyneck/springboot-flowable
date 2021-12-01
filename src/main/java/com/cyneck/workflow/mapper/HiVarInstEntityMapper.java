package com.cyneck.workflow.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyneck.workflow.model.entity.ActHiVarinstEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 历史表单数据实体
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/26 16:16
 **/
public interface HiVarInstEntityMapper extends BaseMapper<ActHiVarinstEntity> {

    @Select("select rv.ID_ id,\n" +
            "       rv.REV_ rev,\n" +
            "       rv.VAR_TYPE_ type,\n" +
            "       rv.NAME_ name,\n" +
            "       rv.EXECUTION_ID_ executionId,\n" +
            "       rv.PROC_INST_ID_ procInstId,\n" +
            "       rv.TASK_ID_ taskId,\n" +
            "       rv.SCOPE_ID_ scopeId,\n" +
            "       rv.SUB_SCOPE_ID_ subScopeId,\n" +
            "       rv.SCOPE_TYPE_ scopeType,\n" +
            "       rv.BYTEARRAY_ID_ byteArrayId,\n" +
            "       rv.DOUBLE_ doubleI,\n" +
            "       rv.LONG_ longI,\n" +
            "       rv.TEXT_ text,\n" +
            "       rv.TEXT2_ text2,\n" +
            "       rv.CREATE_TIME_ createTime,\n" +
            "       rv.LAST_UPDATED_TIME_ lastUpdatedTime\n" +
            "from act_hi_varinst rv\n" +
            "where rv.`PROC_INST_ID_` = #{proccessInstanceId};")
    List<ActHiVarinstEntity> findListByProcInstId(@Param("proccessInstanceId") String proccessInstanceId);
}
