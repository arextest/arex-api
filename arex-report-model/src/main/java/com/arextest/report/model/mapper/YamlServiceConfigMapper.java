package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.config.record.ServiceCollectConfiguration;
import com.arextest.report.model.api.contracts.config.yamlTemplate.ServiceConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mapper
public interface YamlServiceConfigMapper {

    YamlServiceConfigMapper INSTANCE = Mappers.getMapper(YamlServiceConfigMapper.class);

    @Mappings({
            @Mapping(target = "excludeOperationMap", qualifiedByName = "toCollection")
    })
    ServiceConfig toYaml(ServiceCollectConfiguration dao);

    @Mappings({
            @Mapping(target = "excludeOperationMap", qualifiedByName = "toSet")
    })
    ServiceCollectConfiguration fromYaml(ServiceConfig dto);

    @Named("toCollection")
    default Map<String, Collection<String>> toCollection(Map<String, Set<String>> excludeOperationMap) {
        Map<String, Collection<String>> map = new HashMap<>();
        if (excludeOperationMap == null) {
            return null;
        }
        excludeOperationMap.forEach((k, v) -> {
            map.put(k, new ArrayList<>(Optional.ofNullable(v).orElse(Collections.emptySet())));
        });
        return map;
    }

    @Named("toSet")
    default Map<String, Set<String>> toSet(Map<String, Collection<String>> excludeOperationMap) {
        Map<String, Set<String>> map = new HashMap<>();
        if (excludeOperationMap == null) {
            return null;
        }
        excludeOperationMap.forEach((k, v) -> {
            map.put(k, new HashSet<>(Optional.ofNullable(v).orElse(Collections.emptySet())));
        });
        return map;
    }
}