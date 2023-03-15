package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.iosummary.CaseSummaryCollection;
import com.arextest.web.model.dto.iosummary.CaseSummary;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Mapper
public interface CaseSummaryMapper {

    CaseSummaryMapper INSTANCE = Mappers.getMapper(CaseSummaryMapper.class);

    CaseSummary dtoFromDao(CaseSummaryCollection dao);

    CaseSummaryCollection daoFromDto(CaseSummary dto);

}
