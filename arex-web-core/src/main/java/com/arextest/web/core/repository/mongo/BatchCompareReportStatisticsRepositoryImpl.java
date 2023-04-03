// package com.arextest.web.core.repository.mongo;
//
// import com.arextest.web.core.repository.BatchCompareReportStatisticsRepository;
// import com.arextest.web.core.repository.mongo.util.MongoHelper;
// import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareSummaryItem;
// import com.arextest.web.model.dao.mongodb.batchcomparereport.BatchCompareReportStatisticsCollection;
// import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportStatisticsDto;
// import com.arextest.web.model.mapper.BatchCompareReportStatisticsMapper;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.mongodb.core.FindAndModifyOptions;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.data.mongodb.core.query.Update;
// import org.springframework.stereotype.Component;
//
// import java.util.List;
// import java.util.stream.Collectors;
//
// /**
//  * Created by rchen9 on 2023/2/9.
//  */
// @Component
// public class BatchCompareReportStatisticsRepositoryImpl implements BatchCompareReportStatisticsRepository {
//
//     private static final String PLAN_ID = "planId";
//     private static final String INTERFACE_ID = "interfaceId";
//     private static final String UNMATCHED_TYPE = "unMatchedType";
//     private static final String FUZZY_PATH = "fuzzyPath";
//     private static final String ERROR_COUNT = "errorCount";
//     private static final String LOG_ID = "logId";
//     private static final String LOG_ENTITY = "logEntity";
//     private static final String CASE_ID = "caseId";
//
//     @Autowired
//     MongoTemplate mongoTemplate;
//
//     @Override
//     public boolean updateBatchCompareReportStatistics(BatchCompareReportStatisticsDto dto) {
//         if (dto == null) {
//             return false;
//         }
//
//         Update update = MongoHelper.getUpdate();
//         update.setOnInsert(CASE_ID, dto.getCaseId());
//         update.setOnInsert(LOG_ID, dto.getLogId());
//         update.setOnInsert(LOG_ENTITY, dto.getLogEntity());
//         update.setOnInsert(DATA_CHANGE_CREATE_TIME, System.currentTimeMillis());
//         update.inc(ERROR_COUNT, dto.getErrorCount());
//         BatchCompareReportStatisticsCollection andModify = mongoTemplate.findAndModify(
//                 Query.query(Criteria.where(PLAN_ID).is(dto.getPlanId())
//                         .and(INTERFACE_ID).is(dto.getInterfaceId())
//                         .and(UNMATCHED_TYPE).is(dto.getUnMatchedType())
//                         .and(FUZZY_PATH).is(dto.getFuzzyPath())),
//                 update,
//                 FindAndModifyOptions.options().upsert(true).returnNew(true),
//                 BatchCompareReportStatisticsCollection.class
//         );
//         return andModify != null;
//     }
//
//     @Override
//     public List<BatchCompareSummaryItem> queryBatchCompareSummary(String planId, String interfaceId) {
//         Query query = Query.query(Criteria.where(PLAN_ID).is(planId)
//                 .and(INTERFACE_ID).is(interfaceId));
//         List<BatchCompareReportStatisticsCollection> daos =
//                 mongoTemplate.find(query, BatchCompareReportStatisticsCollection.class);
//         return daos.stream().map(BatchCompareReportStatisticsMapper.INSTANCE::itemFromDao)
//                 .collect(Collectors.toList());
//     }
// }
