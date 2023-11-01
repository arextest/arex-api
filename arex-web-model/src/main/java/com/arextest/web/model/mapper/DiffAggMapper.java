package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.ReportDiffAggStatisticCollection;
import com.arextest.web.model.dao.mongodb.entity.SceneDetail;
import com.arextest.web.model.dto.DiffAggDto;
import com.arextest.web.model.dto.SceneDetailDto;
import java.util.HashMap;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DiffAggMapper {

  DiffAggMapper INSTANCE = Mappers.getMapper(DiffAggMapper.class);

  ReportDiffAggStatisticCollection daoFromDto(DiffAggDto dto);

  DiffAggDto dtoFromDao(ReportDiffAggStatisticCollection dao);

  default Map<String, Map<String, SceneDetail>> map1(Map<String, Map<String, SceneDetailDto>> dto) {
    if (dto == null) {
      return null;
    }
    Map<String, Map<String, SceneDetail>> result = new HashMap<>();
    dto.forEach((key, value) -> {
      if (value != null) {
        Map<String, SceneDetail> s = new HashMap<>();
        value.forEach((k, v) -> {
          SceneDetail sd = SceneDetailMapper.INSTANCE.daoFromDto(v);
          s.put(k, sd);
        });
        result.put(key, s);
      }
    });
    return result;
  }

  default Map<String, Map<String, SceneDetailDto>> map(Map<String, Map<String, SceneDetail>> dao) {
    if (dao == null) {
      return null;
    }
    Map<String, Map<String, SceneDetailDto>> result = new HashMap<>();
    dao.forEach((key, value) -> {
      if (value != null) {
        Map<String, SceneDetailDto> s = new HashMap<>();
        value.forEach((k, v) -> {
          SceneDetailDto sd = SceneDetailMapper.INSTANCE.dtoFromDao(v);
          s.put(k, sd);
        });
        result.put(key, s);
      }
    });
    return result;
  }
}
