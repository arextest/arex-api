package com.arextest.report.model.mapper;


import com.arextest.report.model.api.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.report.model.dao.mongodb.ServiceOperationCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceOperationMapper {
    ServiceOperationMapper INSTANCE = Mappers.getMapper(ServiceOperationMapper.class);

    @Mappings({
            @Mapping(target = "modifiedTime", expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")
    })
    ApplicationOperationConfiguration dtoFromDao(ServiceOperationCollection dao);

    @Mappings({
            @Mapping(target = "modifiedTime", expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))"),
            @Mapping(target = "operationResponse", expression = "java(null)")
    })
    ApplicationOperationConfiguration baseInfoFromDao(ServiceOperationCollection dao);

    @Mappings({
            @Mapping(target = "id", expression = "java(null)"),
            @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
            @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")
    })
    ServiceOperationCollection daoFromDto(ApplicationOperationConfiguration dto);
}
