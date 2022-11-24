package com.arextest.web.core.business.compare;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.contract.contracts.common.NodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rchen9 on 2022/7/1.
 */
@Mapper
public interface LogEntityMapper {

    LogEntityMapper INSTANCE = Mappers.getMapper(LogEntityMapper.class);

    LogEntity fromLogEntity(com.arextest.diff.model.log.LogEntity logEntity);

    NodeEntity nodeEntityConvert(com.arextest.diff.model.log.NodeEntity entity);

    default List<List<NodeEntity>> map(List<List<com.arextest.diff.model.log.NodeEntity>> list) {
        List<List<NodeEntity>> results = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return results;
        }
        for (List<com.arextest.diff.model.log.NodeEntity> subList : list) {
            if (subList == null) {
                continue;
            }
            List<NodeEntity> subResults = new ArrayList<>();
            for (com.arextest.diff.model.log.NodeEntity entity : subList) {
                subResults.add(INSTANCE.nodeEntityConvert(entity));
            }
            results.add(subResults);
        }
        return results;
    }
}
