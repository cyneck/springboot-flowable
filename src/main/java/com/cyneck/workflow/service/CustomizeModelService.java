package com.cyneck.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyneck.workflow.model.entity.ActDeModelEntity;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/22 10:35
 **/

public interface CustomizeModelService extends IService<ActDeModelEntity> {

    /**
     * 获取模型及其关联的模型
     *
     * @param modelKey
     * @param modelType
     * @return
     */
    ActDeModelEntity getModelAndRelationByKey(String modelKey, Integer modelType);


    /**
     * 获取模型
     *
     * @param modelKey
     * @param modelType
     * @return
     */
    ActDeModelEntity getModelEntityByKey(String modelKey, Integer modelType);

}
