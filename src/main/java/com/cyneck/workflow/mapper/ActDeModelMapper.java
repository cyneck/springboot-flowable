package com.cyneck.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyneck.workflow.model.entity.ActDeModelEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * (act_de_model)数据Mapper
 *
 * @author eric.lee
 * @description 由 Mybatisplus Code Generator 创建
 * @since 2021-01-22 10:56:23
 */
@Mapper
public interface ActDeModelMapper extends BaseMapper<ActDeModelEntity> {


    @Select("SELECT m.id id,\n" +
            "         m.`name` `name`,\n" +
            "         m.model_key modelKey ,\n" +
            "         m.description description,\n" +
            "         m.model_comment modelComment,\n" +
            "         m.created created,\n" +
            "         m.created_by createdBy,\n" +
            "         m.last_updated lastUpdated,\n" +
            "         m.last_updated_by lastUpdatedBy,\n" +
            "         m.version version,\n" +
            "         m.model_editor_json modelEditorJson,\n" +
            "         m.thumbnail thumbnail,\n" +
            "         m.model_type modelType,\n" +
            "         m.tenant_id tenantId\n" +
            "FROM act_de_model m\n" +
            "WHERE m.model_key = #{modelKey} and m.model_type = #{modelType};")
    ActDeModelEntity getModelEntityByKey(@Param("modelKey") String modelKey,@Param("modelType") Integer modelType);

    @Select("SELECT m.id id,\n" +
            "         m.`name` `name`,\n" +
            "         m.model_key modelKey ,\n" +
            "         m.description description,\n" +
            "         m.model_comment modelComment,\n" +
            "         m.created created,\n" +
            "         m.created_by createdBy,\n" +
            "         m.last_updated lastUpdated,\n" +
            "         m.last_updated_by lastUpdatedBy,\n" +
            "         m.version version,\n" +
            "         m.model_editor_json modelEditorJson,\n" +
            "         m.thumbnail thumbnail,\n" +
            "         m.model_type modelType,\n" +
            "         m.tenant_id tenantId\n" +
            "FROM act_de_model m\n" +
            "WHERE m.id IN \n" +
            "    (SELECT mr.model_id\n" +
            "    FROM act_de_model_relation mr\n" +
            "    WHERE mr.parent_model_id = #{parentModelId});")
    List<ActDeModelEntity> getModelChildrenByParentId(@Param("parentModelId") String parentModelId);


}
