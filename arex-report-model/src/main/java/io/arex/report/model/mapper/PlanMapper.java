package io.arex.report.model.mapper;

import io.arex.report.model.api.contracts.ReportInitialRequestType;
import io.arex.report.model.api.contracts.common.PlanStatistic;
import io.arex.report.model.dao.mongodb.ReportPlanStatisticCollection;
import io.arex.report.model.dto.ReportPlanStatisticDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper
public interface PlanMapper {
    PlanMapper INSTANCE = Mappers.getMapper(PlanMapper.class);

    @Mappings({
            @Mapping(target = "appId", source = "application.appId"),
            @Mapping(target = "appName", source = "application.appName"),
            @Mapping(target = "sourceEnv", source = "hostEnv.sourceEnv"),
            @Mapping(target = "targetEnv", source = "hostEnv.targetEnv"),
            @Mapping(target = "sourceHost", source = "hostEnv.sourceHost"),
            @Mapping(target = "targetHost", source = "hostEnv.targetHost"),
            @Mapping(target = "caseSourceType", source = "caseSourceEnv.caseSourceType"),
            @Mapping(target = "caseStartTime", source = "caseSourceEnv.caseStartTime"),
            @Mapping(target = "caseEndTime", source = "caseSourceEnv.caseEndTime"),
            @Mapping(target = "targetImageId", source = "targetImage.targetImageId"),
            @Mapping(target = "targetImageName", source = "targetImage.targetImageName"),
            @Mapping(target = "coreVersion", source = "version.coreVersion"),
            @Mapping(target = "extVersion", source = "version.extVersion"),
            @Mapping(target = "caseRecordVersion", source = "version.caseRecordVersion"),
    })
    ReportPlanStatisticDto dtoFromContract(ReportInitialRequestType contract);

    ReportPlanStatisticDto dtoFromDao(ReportPlanStatisticCollection dao);

    PlanStatistic contractFromDto(ReportPlanStatisticDto dto);
}
