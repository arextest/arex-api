package com.arextest.web.model.mapper;


import com.arextest.web.model.contract.contracts.datadesensitization.DesensitizationJar;
import com.arextest.web.model.dao.mongodb.DesensitizationJarCollection;
import com.arextest.web.model.dto.DesensitizationJarDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Mapper
public interface DesensitizationJarMapper {

    DesensitizationJarMapper INSTANCE = Mappers.getMapper(DesensitizationJarMapper.class);

    DesensitizationJarDto dtoFromDao(DesensitizationJarCollection dao);

    @Mappings({
            @Mapping(target = "id", expression = "java(null)"),
            @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
            @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())"),
            @Mapping(target = "uploadDate", expression = "java(new java.util.Date())"),
    })
    DesensitizationJarCollection daoFromDto(DesensitizationJarDto dto);

    DesensitizationJar contractFromDto(DesensitizationJarDto dao);
}
