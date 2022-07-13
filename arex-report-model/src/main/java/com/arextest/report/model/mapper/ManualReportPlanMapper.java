package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.manualreport.InitManualReportRequestType;
import com.arextest.report.model.dao.mongodb.ManualReportPlanCollection;
import com.arextest.report.model.dto.manualreport.ManualReportPlanDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ManualReportPlanMapper {
    ManualReportPlanMapper INSTANCE = Mappers.getMapper(ManualReportPlanMapper.class);

    ManualReportPlanDto dtoFromDao(ManualReportPlanCollection dao);

    ManualReportPlanCollection daoFromDto(ManualReportPlanDto dto);

    ManualReportPlanDto dtoFromContract(InitManualReportRequestType request);
}
