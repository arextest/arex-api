package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.config.replay.ScheduleConfiguration;
import com.arextest.report.model.api.contracts.config.yamlTemplate.ReplayConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2022/9/27.
 */
@Mapper
public interface YamlReplayConfigMapper {
    YamlReplayConfigMapper INSTANCE = Mappers.getMapper(YamlReplayConfigMapper.class);

    ReplayConfig toYaml(ScheduleConfiguration scheduleConfiguration);

    ScheduleConfiguration fromYaml(ReplayConfig replayConfig);
}
