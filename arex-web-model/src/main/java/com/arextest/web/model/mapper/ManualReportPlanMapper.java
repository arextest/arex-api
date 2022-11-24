package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.manualreport.InitManualReportRequestType;
import com.arextest.web.model.dao.mongodb.ManualReportPlanCollection;
import com.arextest.web.model.dto.manualreport.ManualReportPlanDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ManualReportPlanMapper {
    ManualReportPlanMapper INSTANCE = Mappers.getMapper(ManualReportPlanMapper.class);

    ManualReportPlanDto dtoFromDao(ManualReportPlanCollection dao);

    ManualReportPlanCollection daoFromDto(ManualReportPlanDto dto);

    ManualReportPlanDto dtoFromContract(InitManualReportRequestType request);
}
