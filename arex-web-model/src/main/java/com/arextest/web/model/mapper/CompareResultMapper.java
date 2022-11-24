package com.arextest.web.model.mapper;

import com.arextest.common.utils.CompressionUtils;
import com.arextest.common.utils.SerializationUtils;
import com.arextest.web.model.contract.contracts.common.CompareResult;
import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.dao.mongodb.ReplayCompareResultCollection;
import com.arextest.web.model.dto.CompareResultDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;


@Mapper
public interface CompareResultMapper {

    CompareResultMapper INSTANCE = Mappers.getMapper(CompareResultMapper.class);

    @Mappings({
            @Mapping(target = "dataChangeCreateTime", expression = "java("
                    + "dto.getDataChangeCreateTime() == null"
                    + "?System.currentTimeMillis()"
                    + ":dto.getDataChangeCreateTime())"
            ),
            @Mapping(target = "dataChangeUpdateTime", expression = "java("
                    + "dto.getDataChangeUpdateTime() == null"
                    + "?System.currentTimeMillis()"
                    + ":dto.getDataChangeUpdateTime())"
            )
    })
    ReplayCompareResultCollection daoFromDto(CompareResultDto dto);

    @Mappings({
            @Mapping(target = "baseMsg", qualifiedByName = "decompressMsg"),
            @Mapping(target = "testMsg", qualifiedByName = "decompressMsg")
    })
    CompareResultDto dtoFromDao(ReplayCompareResultCollection dao);

    @Mappings({
            @Mapping(target = "dataCreateTime", expression = "java(new java.util.Date())"),
    })
    CompareResultDto dtoFromContract(CompareResult contract);

    CompareResult contractFromDto(CompareResultDto dto);

    @Mappings({
            @Mapping(target = "logs", expression = "java(dto.getDiffResultCode() == 2 ? dto.getLogs() : null)"),
    })
    CompareResult contractFromDtoLogsLimitDisplay(CompareResultDto dto);

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
