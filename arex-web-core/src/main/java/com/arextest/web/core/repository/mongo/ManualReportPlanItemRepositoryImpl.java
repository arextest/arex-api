package com.arextest.web.core.repository.mongo;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.arextest.web.core.repository.ManualReportPlanItemRepository;
import com.arextest.web.model.dao.mongodb.ManualReportPlanItemCollection;
import com.arextest.web.model.dto.manualreport.ManualReportPlanItemDto;
import com.arextest.web.model.mapper.ManualReportPlanItemMapper;

@Component
public class ManualReportPlanItemRepositoryImpl implements ManualReportPlanItemRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public ManualReportPlanItemDto initManualReportPlanItems(ManualReportPlanItemDto planItemDto) {
        ManualReportPlanItemCollection dao = ManualReportPlanItemMapper.INSTANCE.daoFromDto(planItemDto);
        dao = mongoTemplate.insert(dao);
        return ManualReportPlanItemMapper.INSTANCE.dtoFromDao(dao);
    }
}
