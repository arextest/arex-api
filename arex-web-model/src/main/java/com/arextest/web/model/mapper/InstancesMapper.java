package com.arextest.web.model.mapper;


import com.arextest.web.model.contract.contracts.config.application.InstancesConfiguration;
import com.arextest.web.model.contract.contracts.config.instance.AgentRemoteConfigurationRequest;
import com.arextest.web.model.contract.contracts.config.instance.AgentStatusRequest;
import com.arextest.web.model.dao.mongodb.InstancesCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper
public interface InstancesMapper {

    InstancesMapper INSTANCE = Mappers.getMapper(InstancesMapper.class);

    @Mappings({
            @Mapping(target = "modifiedTime", expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")
    })
    InstancesConfiguration dtoFromDao(InstancesCollection dao);

    InstancesCollection daoFromDto(InstancesConfiguration dto);

    InstancesConfiguration dtoFromContract(AgentRemoteConfigurationRequest contract);

    InstancesConfiguration dtoFromContract(AgentStatusRequest request);
}
