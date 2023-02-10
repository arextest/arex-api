package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.BatchCompareReportResultRepository;
import com.arextest.web.model.dao.mongodb.batchcomparereport.BatchCompareReportResultCollection;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportResultDto;
import com.arextest.web.model.mapper.BatchCompareReportResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2023/2/9.
 */
@Component
public class BatchCompareReportResultRepositoryImpl implements BatchCompareReportResultRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<String> insertAll(List<BatchCompareReportResultDto> batchCompareReportResultDtoList) {
        List<BatchCompareReportResultCollection> daos = batchCompareReportResultDtoList.stream()
                .map(BatchCompareReportResultMapper.INSTANCE::daoFromDto)
                .collect(Collectors.toList());
        Collection<BatchCompareReportResultCollection> batchCompareReportResultCollections =
                mongoTemplate.insertAll(daos);
        return batchCompareReportResultCollections.stream()
                .map(BatchCompareReportResultCollection::getId)
                .collect(Collectors.toList());
    }
}
