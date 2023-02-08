package com.arextest.web.model.mapper;

import cn.hutool.json.JSONUtil;
import com.arextest.common.utils.CompressionUtils;
import com.arextest.web.model.dao.mongodb.FSTraceLogCollection;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTraceLogDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author b_yu
 * @since 2023/1/18
 */
@Mapper
public interface FSTraceLogMapper {
    FSTraceLogMapper INSTANCE = Mappers.getMapper(FSTraceLogMapper.class);

    FSTraceLogCollection daoFromDto(FSTraceLogDto dto);

    FSTraceLogDto dtoFromDao(FSTraceLogCollection dao);

    default String map(FSNodeDto dto) {
        return CompressionUtils.useZstdCompress(JSONUtil.toJsonStr(dto));
    }

    default FSNodeDto mapNode(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return JSONUtil.toBean(CompressionUtils.useZstdDecompress(str), FSNodeDto.class);
    }

    default List<FSItemDto> mapItems(String str) throws JsonProcessingException {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(CompressionUtils.useZstdDecompress(str), new TypeReference<List<FSItemDto>>() {
        });
    }

    default String map(List<FSItemDto> items) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerFor(new TypeReference<List<FSItemDto>>() {
        });
        return CompressionUtils.useZstdCompress(objectWriter.writeValueAsString(items));
    }
}
