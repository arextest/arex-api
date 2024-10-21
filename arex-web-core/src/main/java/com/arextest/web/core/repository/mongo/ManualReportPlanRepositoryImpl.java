package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.ManualReportPlanRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.ManualReportPlanCollection;
import com.arextest.web.model.dto.manualreport.ManualReportPlanDto;
import com.arextest.web.model.mapper.ManualReportPlanMapper;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class ManualReportPlanRepositoryImpl implements ManualReportPlanRepository {

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public ManualReportPlanDto initManualReportPlan(ManualReportPlanDto dto) {
    ManualReportPlanCollection dao = ManualReportPlanMapper.INSTANCE.daoFromDto(dto);
    MongoHelper.initInsertObject(dao);
    ManualReportPlanCollection result = mongoTemplate.insert(dao);
    return ManualReportPlanMapper.INSTANCE.dtoFromDao(result);
  }
}
