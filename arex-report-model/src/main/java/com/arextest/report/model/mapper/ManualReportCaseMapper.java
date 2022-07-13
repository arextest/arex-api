package com.arextest.report.model.mapper;

import com.arextest.common.utils.CompressionUtils;
import com.arextest.common.utils.SerializationUtils;
import com.arextest.report.model.api.contracts.common.LogEntity;
import com.arextest.report.model.api.contracts.manualreport.ReportCaseType;
import com.arextest.report.model.dao.mongodb.ManualReportCaseCollection;
import com.arextest.report.model.dto.filesystem.FSCaseDto;
import com.arextest.report.model.dto.manualreport.ManualReportCaseDto;
import com.arextest.report.model.dto.manualreport.SaveManualReportCaseDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

@Mapper
public interface ManualReportCaseMapper {
    ManualReportCaseMapper INSTANCE = Mappers.getMapper(ManualReportCaseMapper.class);

    List<ManualReportCaseDto> dtoFromFsCaseDto(List<FSCaseDto> dtos);

    List<ManualReportCaseCollection> daoFromDtoList(List<ManualReportCaseDto> dtos);

    @Mappings({
            @Mapping(target = "baseMsg", qualifiedByName = "compressMsg"),
            @Mapping(target = "testMsg", qualifiedByName = "compressMsg")
    })
    ManualReportCaseCollection daoFromDto(SaveManualReportCaseDto dto);

    ReportCaseType contractFromDto(ManualReportCaseDto dto);

    ManualReportCaseDto dtoFromDao(ManualReportCaseCollection dao);

    @Named("compressMsg")
    default String compressMsg(String decompressString) {
        return CompressionUtils.useZstdCompress(decompressString);
    }

    @Named("decompressMsg")
    default String decompressMsg(String compressString) {
        return CompressionUtils.useZstdDecompress(compressString);
    }

    default String map(List<LogEntity> logs) {
        if (logs == null) {
            return StringUtils.EMPTY;
        }
        return SerializationUtils.useZstdSerializeToBase64(logs.toArray());
    }

    default List<LogEntity> map(String logs) {
        LogEntity[] logEntities = SerializationUtils.useZstdDeserialize(logs, LogEntity[].class);
        if (logEntities == null) {
            return null;
        }
        return Arrays.asList(logEntities);
    }
}
