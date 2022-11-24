package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.manualreport.ReportInterfaceType;
import com.arextest.web.model.dao.mongodb.ManualReportPlanItemCollection;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.manualreport.ManualReportPlanItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ManualReportPlanItemMapper {
    ManualReportPlanItemMapper INSTANCE = Mappers.getMapper(ManualReportPlanItemMapper.class);

    ManualReportPlanItemDto dtoFromFsInterfaceDto(FSInterfaceDto dto);

    ManualReportPlanItemDto dtoFromDao(ManualReportPlanItemCollection dao);

    ManualReportPlanItemCollection daoFromDto(ManualReportPlanItemDto dto);

    ReportInterfaceType contractFromDto(ManualReportPlanItemDto dto);
}
