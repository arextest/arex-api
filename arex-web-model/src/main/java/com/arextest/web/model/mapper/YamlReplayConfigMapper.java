package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.ReplayTemplateConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2022/9/27.
 */
@Mapper
public interface YamlReplayConfigMapper {

  YamlReplayConfigMapper INSTANCE = Mappers.getMapper(YamlReplayConfigMapper.class);

  @Mappings({@Mapping(target = "excludeOperationMap", qualifiedByName = "toCollection")})
  ReplayTemplateConfig toYaml(ScheduleConfiguration scheduleConfiguration);

  @Mappings({@Mapping(target = "excludeOperationMap", qualifiedByName = "toSet")})
  ScheduleConfiguration fromYaml(ReplayTemplateConfig replayTemplateConfig);

  @Named("toCollection")
  default Map<String, Collection<String>> toCollection(
      Map<String, Set<String>> excludeOperationMap) {
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
