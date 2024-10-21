package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.ManualReportCaseRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.ManualReportCaseCollection;
import com.arextest.web.model.dto.manualreport.ManualReportCaseDto;
import com.arextest.web.model.dto.manualreport.SaveManualReportCaseDto;
import com.arextest.web.model.mapper.ManualReportCaseMapper;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class ManualReportCaseRepositoryImpl implements ManualReportCaseRepository {

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public List<ManualReportCaseDto> initManualReportCases(List<ManualReportCaseDto> caseDtos) {
    List<ManualReportCaseCollection> daos = ManualReportCaseMapper.INSTANCE.daoFromDtoList(
        caseDtos);
    Collection<ManualReportCaseCollection> results = mongoTemplate.insertAll(daos);
    return results.stream().map(ManualReportCaseMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ManualReportCaseDto> queryManualReportCases(List<String> ids) {
    Collection<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id))
        .collect(Collectors.toSet());
    Query query = Query.query(Criteria.where(DASH_ID).in(objectIds));
    List<ManualReportCaseCollection> daos = mongoTemplate.find(query,
        ManualReportCaseCollection.class);
    return daos.stream().map(ManualReportCaseMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public boolean saveManualReportCaseResult(SaveManualReportCaseDto caseDto) {
    Query query = Query.query(Criteria.where(DASH_ID).is(caseDto.getId()));
    Update update = MongoHelper.getUpdate();

    ManualReportCaseCollection dao = ManualReportCaseMapper.INSTANCE.daoFromDto(caseDto);
    MongoHelper.appendFullProperties(update, dao);

    try {
      mongoTemplate.findAndModify(query, update, ManualReportCaseCollection.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
