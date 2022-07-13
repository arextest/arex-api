package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.manualreport.ReportInterfaceType;
import com.arextest.report.model.dao.mongodb.ManualReportPlanItemCollection;
import com.arextest.report.model.dto.filesystem.FSInterfaceDto;
import com.arextest.report.model.dto.manualreport.ManualReportPlanItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ManualReportPlanItemMapper {
    ManualReportPlanItemMapper INSTANCE = Mappers.getMapper(ManualReportPlanItemMapper.class);

    ManualReportPlanItemDto dtoFromFsInterfaceDto(FSInterfaceDto dto);

    ManualReportPlanItemDto dtoFromDao(ManualReportPlanItemCollection dao);

    ManualReportPlanItemCollection daoFromDto(ManualReportPlanItemDto dto);

    ReportInterfaceType contractFromDto(ManualReportPlanItemDto dto);
}
