// package com.arextest.web.core.repository.mongo;
//
// import com.arextest.web.core.repository.BatchCompareReportRepository;
// import com.arextest.web.core.repository.mongo.util.MongoHelper;
// import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareInterfaceProcess;
// import com.arextest.web.model.dao.mongodb.batchcomparereport.BatchCompareReportCaseCollection;
// import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportCaseDto;
// import com.arextest.web.model.mapper.BatchCompareReportCaseMapper;
// import com.mongodb.BasicDBObject;
// import org.apache.commons.collections4.CollectionUtils;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.aggregation.Aggregation;
// import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
// import org.springframework.data.mongodb.core.aggregation.AggregationResults;
// import org.springframework.data.mongodb.core.aggregation.GroupOperation;
// import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.data.mongodb.core.query.Update;
// import org.springframework.stereotype.Component;
//
// import java.util.ArrayList;
// import java.util.Collection;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;
//
// /**
//  * Created by rchen9 on 2023/2/7.
//  */
// @Component
// public class BatchCompareReportRepositoryImpl implements BatchCompareReportRepository {
//
//     private static final String PLAN_ID = "planId";
//     private static final String INTERFACE_ID = "interfaceId";
//     private static final String INTERFACE_NAME = "interfaceName";
//     private static final String CASE_ID = "caseId";
//     private static final String STATUS = "status";
//     private static final String CASEID_LIST = "caseIdList";
//
//     @Autowired
//     MongoTemplate mongoTemplate;
//
//     public boolean insertAll(List<BatchCompareReportCaseDto> batchCompareReportCaseDtoList) {
//
//         List<BatchCompareReportCaseCollection> batchCompareReportCaseCollections =
//                 batchCompareReportCaseDtoList.stream()
//                         .map(BatchCompareReportCaseMapper.INSTANCE::daoFromDto)
//                         .collect(Collectors.toList());
//
//         Collection<BatchCompareReportCaseCollection> batchCompareReportCaseDtos =
//                 mongoTemplate.insertAll(batchCompareReportCaseCollections);
//         return CollectionUtils.isNotEmpty(batchCompareReportCaseDtos);
//     }
//
//     public boolean updateBatchCompareCase(BatchCompareReportCaseDto batchCompareReportCaseDto) {
//         Query query = Query.query(Criteria.where(PLAN_ID).is(batchCompareReportCaseDto.getPlanId())
//                 .and(INTERFACE_ID).is(batchCompareReportCaseDto.getInterfaceId())
//                 .and(CASE_ID).is(batchCompareReportCaseDto.getCaseId()));
//         Update update = MongoHelper.getUpdate();
//         BatchCompareReportCaseCollection dao =
//                 BatchCompareReportCaseMapper.INSTANCE.daoFromDto(batchCompareReportCaseDto);
//         MongoHelper.appendFullProperties(update, dao);
//         mongoTemplate.findAndModify(query, update, BatchCompareReportCaseCollection.class);
//         return true;
//     }
//
//     @Override
//     public List<BatchCompareInterfaceProcess> queryBatchCompareProgress(String planId) {
//         List<AggregationOperation> operations = new ArrayList<>();
//         operations.add(Aggregation.match(Criteria.where(PLAN_ID).is(planId)));
//
//         GroupOperation groupOperation = Aggregation.group(INTERFACE_ID, INTERFACE_NAME, STATUS)
//                 .first(INTERFACE_ID).as(INTERFACE_ID)
//                 .first(INTERFACE_NAME).as(INTERFACE_NAME)
//                 .first(STATUS).as(STATUS)
//                 .addToSet(CASE_ID).as(CASEID_LIST);
//         operations.add(groupOperation);
//
//         ProjectionOperation projectionOperation =
//                 Aggregation.project(INTERFACE_ID, INTERFACE_NAME, STATUS, CASEID_LIST);
//         operations.add(projectionOperation);
//         AggregationResults<BasicDBObject> aggregate = mongoTemplate.aggregate(Aggregation.newAggregation(operations),
//                 BatchCompareReportCaseCollection.class, BasicDBObject.class);
//         return this.covertToBatchCompareInterfaceProcess(aggregate.getMappedResults());
//     }
//
//     @Override
//     public BatchCompareReportCaseDto findById(String planId, String interfaceId, String caseId) {
//         Query query = Query.query(Criteria.where(PLAN_ID).is(planId)
//                 .and(INTERFACE_ID).is(interfaceId)
//                 .and(CASE_ID).is(caseId));
//         BatchCompareReportCaseCollection dao = mongoTemplate.findOne(query, BatchCompareReportCaseCollection.class);
//         return BatchCompareReportCaseMapper.INSTANCE.dtoFromDao(dao);
//     }
//
//     private List<BatchCompareInterfaceProcess> covertToBatchCompareInterfaceProcess(List<BasicDBObject> mappedResults) {
//         List<BatchCompareInterfaceProcess> result = new ArrayList<>();
//         if (CollectionUtils.isEmpty(mappedResults)) {
//             return result;
//         }
//
//         Map<String, List<BasicDBObject>> interfaceMap = mappedResults.stream()
//                 .collect(Collectors.groupingBy(item -> item.getString(INTERFACE_ID)));
//         for (Map.Entry<String, List<BasicDBObject>> entry : interfaceMap.entrySet()) {
//             String interfaceId = entry.getKey();
//             List<BasicDBObject> statusList = entry.getValue();
//             String interfaceName = statusList.get(0).getString(INTERFACE_NAME);
//
//             List<BatchCompareInterfaceProcess.StatusStatistic> statusStatistics = new ArrayList<>();
//             statusList.forEach(item -> {
//                 int status = item.getInt(STATUS);
//                 List<String> caseIds = (List<String>) item.get(CASEID_LIST);
//                 BatchCompareInterfaceProcess.StatusStatistic statusStatistic =
//                         new BatchCompareInterfaceProcess.StatusStatistic();
//                 statusStatistic.setStatus(status);
//                 statusStatistic.setCount(caseIds.size());
//                 statusStatistic.setCaseIdList(caseIds);
//                 statusStatistics.add(statusStatistic);
//             });
//             BatchCompareInterfaceProcess batchCompareInterfaceProcess = new BatchCompareInterfaceProcess();
//             batchCompareInterfaceProcess.setInterfaceId(interfaceId);
//             batchCompareInterfaceProcess.setInterfaceName(interfaceName);
//             batchCompareInterfaceProcess.setStatusList(statusStatistics);
//             result.add(batchCompareInterfaceProcess);
//         }
//         return result;
//     }
// }
