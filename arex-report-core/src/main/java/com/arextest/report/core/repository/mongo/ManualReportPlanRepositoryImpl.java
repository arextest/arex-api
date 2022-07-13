package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.ManualReportPlanRepository;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.dao.mongodb.ManualReportPlanCollection;
import com.arextest.report.model.dto.manualreport.ManualReportPlanDto;
import com.arextest.report.model.mapper.ManualReportPlanMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
