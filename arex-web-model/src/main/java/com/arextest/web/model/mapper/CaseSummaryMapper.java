package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.dao.mongodb.iosummary.CaseSummaryCollection;
import com.arextest.web.model.dto.iosummary.CaseSummary;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Mapper
public interface CaseSummaryMapper {

    CaseSummaryMapper INSTANCE = Mappers.getMapper(CaseSummaryMapper.class);

    CaseSummary dtoFromDao(CaseSummaryCollection dao);

    @Mappings({@Mapping(target = "dataCreateTime", expression = "java(new java.util.Date())"),})
    CaseSummaryCollection daoFromDto(CaseSummary dto);

}
