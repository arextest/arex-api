//package com.arextest.web.core.business.config.replay;
//
//import com.arextest.web.core.repository.mongo.ComparisonExclusionsConfigurationRepositoryImpl;
//import com.arextest.web.core.repository.mongo.ComparisonIgnoreCategoryConfigurationRepositoryImpl;
//import com.arextest.web.core.repository.mongo.ComparisonInclusionsConfigurationRepositoryImpl;
//import com.arextest.web.core.repository.mongo.ComparisonListSortConfigurationRepositoryImpl;
//import com.arextest.web.core.repository.mongo.ComparisonReferenceConfigurationRepositoryImpl;
//import com.arextest.web.core.repository.mongo.ComparisonTransformConfigurationRepositoryImpl;
//import com.arextest.web.model.contract.contracts.config.replay.ComparisonCategoryType;
//import com.arextest.web.model.contract.contracts.config.replay.QueryConfigByTypeRequestType;
//import com.arextest.web.model.contract.contracts.config.replay.QueryConfigByTypeResponseType;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class PageComparisonSummaryService {
//
//  @Resource
//  private ComparisonExclusionsConfigurationRepositoryImpl comparisonExclusionsConfigurationRepository;
//
//  @Resource
//  private ComparisonInclusionsConfigurationRepositoryImpl comparisonInclusionsConfigurationRepository;
//
//  @Resource
//  private ComparisonIgnoreCategoryConfigurationRepositoryImpl comparisonIgnoreCategoryConfigurationRepository;
//
//  @Resource
//  private ComparisonListSortConfigurationRepositoryImpl comparisonListSortConfigurationRepository;
//
//  @Resource
//  private ComparisonReferenceConfigurationRepositoryImpl comparisonReferenceConfigurationRepository;
//
//  @Resource
//  private ComparisonTransformConfigurationRepositoryImpl comparisonTransformConfigurationRepository;
//
//
//  public QueryConfigByTypeResponseType queryCompareConfigByCategory(ComparisonCategoryType category,
//      QueryConfigByTypeRequestType request) {
//    switch (category) {
//      case EXCLUSION:
//        return comparisonExclusionsConfigurationRepository.findByAppId(appId);
//      case INCLUSION:
//        return comparisonInclusionsConfigurationRepository.findByAppId(appId);
//      case IGNORE_CATEGORY:
//        return comparisonIgnoreCategoryConfigurationRepository.findByAppId(appId);
//      case LIST_SORT:
//        return comparisonListSortConfigurationRepository.findByAppId(appId);
//      case REFERENCE:
//        return comparisonReferenceConfigurationRepository.findByAppId(appId);
//      case ROOT_TRANSFORM:
//        return comparisonTransformConfigurationRepository.findByAppId(appId);
//      default:
//        log.warn("Unknown category: {}", category);
//        return Collections.emptyList();
//    }
//
//    return new QueryConfigByTypeResponseType();
//  }
//
//  protected QueryConfigByTypeResponseType queryExclusionConfig(QueryConfigByTypeRequestType request) {
////    return queryCompareConfigByCategory(ComparisonCategoryType.EXCLUSION, request);
//    return null;
//  }
//
//  protected QueryConfigByTypeResponseType queryInclusionConfig(QueryConfigByTypeRequestType request) {
//    return null;
//  }
//
//  protected QueryConfigByTypeResponseType queryIgnoreCategoryConfig(QueryConfigByTypeRequestType request) {
//    return null;
//  }
//
//  protected QueryConfigByTypeResponseType queryListSortConfig(QueryConfigByTypeRequestType request) {
//    return null;
//  }
//
//  protected QueryConfigByTypeResponseType queryReferenceConfig(QueryConfigByTypeRequestType request) {
//    return null;
//  }
//
//  protected QueryConfigByTypeResponseType queryRootTransformConfig(QueryConfigByTypeRequestType request) {
//    return null;
//  }
//
//  protected QueryConfigByTypeResponseType queryConfigOfCategory(QueryConfigByTypeRequestType request) {
//    return null;
//  }
//
//  protected QueryConfigByTypeResponseType queryCompareConfig(QueryConfigByTypeRequestType request) {
//    return null;
//  }
//
//
//
//
//
//
//
//}