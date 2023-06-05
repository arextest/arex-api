package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.replay.UpdateReportInfoRequestType;
import com.arextest.web.model.dao.mongodb.ReportPlanItemStatisticCollection;
import com.arextest.web.model.dto.PlanItemDto;
import com.arextest.web.model.contract.contracts.ReportInitialRequestType;
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

    PlanItemDto dtoFromContract(UpdateReportInfoRequestType.UpdateReportItem contract);
}
