package com.arextest.web.model.mapper;


import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import com.arextest.web.model.dao.mongodb.ReplayScheduleConfigCollection;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Mapper
public interface ReplayScheduleConfigMapper {
    ReplayScheduleConfigMapper INSTANCE = Mappers.getMapper(ReplayScheduleConfigMapper.class);

    @Mappings({
            @Mapping(target = "modifiedTime", expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))"),
            @Mapping(target = "excludeOperationMap", qualifiedByName = "deserializeMap")
    })
    ScheduleConfiguration dtoFromDao(ReplayScheduleConfigCollection dao);

    @Mappings({
            @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
            @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())"),
            @Mapping(target = "excludeOperationMap", qualifiedByName = "serializeMap")
    })
    ReplayScheduleConfigCollection daoFromDto(ScheduleConfiguration dto);

    @Named("serializeMap")
    default String serializeMap(Map<String, Set<String>> excludeOperationMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        String serializeMsg = null;
        try {
            serializeMsg = objectMapper.writeValueAsString(excludeOperationMap);
        } catch (Exception e) {
        }
        return serializeMsg;
    }

    @Named("deserializeMap")
    default Map<String, Set<String>> deserializeMap(String excludeOperationMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Set<String>> map = new HashMap<>();
        try {
            Map map1 = objectMapper.readValue(excludeOperationMap, Map.class);
            Optional.ofNullable(map1).orElse(Collections.emptyMap()).forEach((k, v) -> {
                map.put((String) k, v == null ? Collections.emptySet() : new HashSet<>((List<String>) v));
            });
        } catch (Exception e) {
            return null;
        }
        return map;
    }
}
