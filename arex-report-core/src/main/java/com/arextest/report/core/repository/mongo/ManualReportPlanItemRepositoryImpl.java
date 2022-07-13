package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.ManualReportPlanItemRepository;
import com.arextest.report.model.dao.mongodb.ManualReportPlanItemCollection;
import com.arextest.report.model.dto.manualreport.ManualReportPlanItemDto;
import com.arextest.report.model.mapper.ManualReportPlanItemMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
