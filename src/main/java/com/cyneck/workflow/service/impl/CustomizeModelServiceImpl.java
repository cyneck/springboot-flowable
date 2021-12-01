package com.cyneck.workflow.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyneck.workflow.mapper.ActDeModelMapper;
import com.cyneck.workflow.model.entity.ActDeModelEntity;
import com.cyneck.workflow.service.CustomizeModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/22 10:53
 **/
@DS("master")
@Service
@Transactional
public class CustomizeModelServiceImpl extends ServiceImpl<ActDeModelMapper, ActDeModelEntity> implements CustomizeModelService {


    @Autowired
    ActDeModelMapper modelMapper;


    @Override
    public ActDeModelEntity getModelAndRelationByKey(String modelKey, Integer modelType) {
        ActDeModelEntity result = modelMapper.getModelEntityByKey(modelKey, modelType);
        List<ActDeModelEntity> children = modelMapper.getModelChildrenByParentId(result.getId());
        for (ActDeModelEntity child : children) {
            List<ActDeModelEntity> formChildren = modelMapper.getModelChildrenByParentId(child.getId());
            child.setChildren(formChildren);
        }
        result.setChildren(children);
        return result;
    }

    @Override
    public ActDeModelEntity getModelEntityByKey(String modelKey, Integer modelType) {
        return modelMapper.getModelEntityByKey(modelKey, modelType);
    }


}
