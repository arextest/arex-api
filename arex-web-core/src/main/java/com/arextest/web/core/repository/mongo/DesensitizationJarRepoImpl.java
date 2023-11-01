package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.DesensitizationJarRepository;
import com.arextest.web.model.dao.mongodb.DesensitizationJarCollection;
import com.arextest.web.model.dto.DesensitizationJarDto;
import com.arextest.web.model.mapper.DesensitizationJarMapper;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @author qzmo
 * @since 2023/8/16
 */
@Slf4j
@Component
public class DesensitizationJarRepoImpl implements DesensitizationJarRepository {

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public boolean saveJar(DesensitizationJarDto dto) {
    DesensitizationJarCollection res = mongoTemplate.save(
        DesensitizationJarMapper.INSTANCE.daoFromDto(dto));
    return StringUtils.isNotBlank(res.getId());
  }

  @Override
  public boolean deleteJar(String jarId) {
    DesensitizationJarCollection res = mongoTemplate.findAndRemove(
        Query.query(Criteria.where(DASH_ID).is(jarId)),
        DesensitizationJarCollection.class);
    return res != null;
  }

  @Override
  public boolean deleteAll() {
    mongoTemplate.remove(new Query(), DesensitizationJarCollection.class);
    return true;
  }

  @Override
  public List<DesensitizationJarDto> queryAll() {
    return mongoTemplate.findAll(DesensitizationJarCollection.class).stream()
        .map(DesensitizationJarMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }
}
