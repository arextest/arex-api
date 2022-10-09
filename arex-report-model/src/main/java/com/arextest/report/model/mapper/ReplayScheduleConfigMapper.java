package com.arextest.report.model.mapper;


import com.arextest.report.model.api.contracts.config.replay.ScheduleConfiguration;
import com.arextest.report.model.dao.mongodb.ReplayScheduleConfigCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ReplayScheduleConfigMapper {
    ReplayScheduleConfigMapper INSTANCE = Mappers.getMapper(ReplayScheduleConfigMapper.class);

    @Mappings({
            @Mapping(target = "modifiedTime", expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")
    })
    ScheduleConfiguration dtoFromDao(ReplayScheduleConfigCollection dao);

    @Mappings({
            @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
            @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")
    })
    ReplayScheduleConfigCollection daoFromDto(ScheduleConfiguration dto);
}
