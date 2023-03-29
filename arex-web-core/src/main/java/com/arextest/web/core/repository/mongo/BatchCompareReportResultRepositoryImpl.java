// package com.arextest.web.core.repository.mongo;
//
// import com.arextest.web.core.repository.BatchCompareReportResultRepository;
// import com.arextest.web.model.dao.mongodb.batchcomparereport.BatchCompareReportResultCollection;
// import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportResultDto;
// import com.arextest.web.model.mapper.BatchCompareReportResultMapper;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.stereotype.Component;
//
// import java.util.Collection;
// import java.util.List;
// import java.util.stream.Collectors;
//
// /**
//  * Created by rchen9 on 2023/2/9.
//  */
// @Component
// public class BatchCompareReportResultRepositoryImpl implements BatchCompareReportResultRepository {
//
//     private static final String PLAN_ID = "planId";
//     private static final String INTERFACE_ID = "interfaceId";
//     private static final String UNMATCHED_TYPE = "unMatchedType";
//     private static final String FUZZY_PATH = "fuzzyPath";
//
//     @Autowired
//     MongoTemplate mongoTemplate;
//
//     @Override
//     public List<String> insertAll(List<BatchCompareReportResultDto> batchCompareReportResultDtoList) {
//         List<BatchCompareReportResultCollection> daos = batchCompareReportResultDtoList.stream()
//                 .map(BatchCompareReportResultMapper.INSTANCE::daoFromDto)
//                 .collect(Collectors.toList());
//         Collection<BatchCompareReportResultCollection> batchCompareReportResultCollections =
//                 mongoTemplate.insertAll(daos);
//         return batchCompareReportResultCollections.stream()
//                 .map(BatchCompareReportResultCollection::getId)
//                 .collect(Collectors.toList());
//     }
//
//     @Override
//     public BatchCompareReportResultDto findById(String id) {
//         Query query = Query.query(Criteria.where(DASH_ID).is(id));
//         BatchCompareReportResultCollection dao = mongoTemplate.findOne(query, BatchCompareReportResultCollection.class);
//         if (dao == null) {
//             return null;
//         }
//         return BatchCompareReportResultMapper.INSTANCE.dtoFromDao(dao);
//     }
//
//     @Override
//     public long countAll(BatchCompareReportResultDto dto) {
//         Query query = Query.query(Criteria.where(PLAN_ID).is(dto.getPlanId())
//                 .and(INTERFACE_ID).is(dto.getInterfaceId())
//                 .and(UNMATCHED_TYPE).is(dto.getUnMatchedType())
//                 .and(FUZZY_PATH).is(dto.getFuzzyPath()));
//         return mongoTemplate.count(query, BatchCompareReportResultCollection.class);
//     }
//
//     @Override
//     public List<BatchCompareReportResultDto> queryAllByPage(BatchCompareReportResultDto dto, int page, int pageSize) {
//         Query query = Query.query(Criteria.where(PLAN_ID).is(dto.getPlanId())
//                 .and(INTERFACE_ID).is(dto.getInterfaceId())
//                 .and(UNMATCHED_TYPE).is(dto.getUnMatchedType())
//                 .and(FUZZY_PATH).is(dto.getFuzzyPath()));
//         query.skip((page - 1) * pageSize).limit(pageSize);
//         List<BatchCompareReportResultCollection> daos =
//                 mongoTemplate.find(query, BatchCompareReportResultCollection.class);
//         return daos.stream().map(BatchCompareReportResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
//     }
// }
