package com.arextest.report.model.mapper;

import com.arextest.report.model.dao.mongodb.ReportPlanItemStatisticCollection;
import com.arextest.report.model.dto.PlanItemDto;
import com.arextest.report.model.api.contracts.ReportInitialRequestType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper
public interface PlanItemMapper {
    PlanItemMapper INSTANCE = Mappers.getMapper(PlanItemMapper.class);

    ReportPlanItemStatisticCollection daoFromDto(PlanItemDto dto);

    PlanItemDto dtoFromDao(ReportPlanItemStatisticCollection dao);

    @Mappings({
            @Mapping(target = "status", constant = "1"),
    })
    PlanItemDto dtoFromContract(ReportInitialRequestType.ReportItem contract);
}
