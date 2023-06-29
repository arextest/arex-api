package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.ReplayDependencyCollection;
import com.arextest.web.model.dto.ReplayDependencyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReplayDependencyMapper {
    ReplayDependencyMapper INSTANCE = Mappers.getMapper(ReplayDependencyMapper.class);

    @Mappings({
            @Mapping(target = "dataChangeCreateTime", expression = "java("
                    + "dto.getDataChangeCreateTime() == null"
                    + "?System.currentTimeMillis()"
                    + ":dto.getDataChangeCreateTime())"
            ),
            @Mapping(target = "dataChangeUpdateTime", expression = "java("
                    + "dto.getDataChangeUpdateTime() == null"
                    + "?System.currentTimeMillis()"
                    + ":dto.getDataChangeUpdateTime())"
            )
    })
    ReplayDependencyCollection daoFromDto(ReplayDependencyDto dto);

    ReplayDependencyDto dtoFromDao(ReplayDependencyCollection dao);
}
