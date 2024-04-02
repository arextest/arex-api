package com.arextest.web.core.business;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.model.replay.CountOperationCaseRequestType;
import com.arextest.model.replay.CountOperationCaseResponseType;
import com.arextest.model.replay.PagedRequestType;
import com.arextest.model.replay.PagedResponseType;
import com.arextest.model.replay.QueryCaseCountRequestType;
import com.arextest.model.replay.QueryCaseCountResponseType;
import com.arextest.model.replay.SortingOption;
import com.arextest.model.replay.SortingTypeEnum;
import com.arextest.web.core.business.beans.httpclient.HttpWebServiceApiClient;
import com.arextest.web.model.contract.contracts.record.AggCountRecordResponseType;
import com.arextest.web.model.contract.contracts.record.CountRecordRequestType;
import com.arextest.web.model.contract.contracts.record.CountRecordResponseType;
import com.arextest.web.model.contract.contracts.record.ListRecordRequestType;
import com.arextest.web.model.contract.contracts.record.ListRecordResponseType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RecordService {

  private static final String CREATE_TIME_COLUMN_NAME = "creationTime";

  @Value("${arex.storage.countRecord.url}")
  private String countRecordUrl;

  @Value("${arex.storage.listRecord.url}")
  private String listRecordUrl;

  @Value("${arex.storage.aggCountRecord.url}")
  private String aggCountRecordUrl;
  @Resource
  private ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

  @Resource
  private HttpWebServiceApiClient httpWebServiceApiClient;

  public CountRecordResponseType countRecord(CountRecordRequestType requestType) {
    QueryCaseCountRequestType queryCaseCountRequestType = new QueryCaseCountRequestType();
    queryCaseCountRequestType.setAppId(requestType.getAppId());
    queryCaseCountRequestType.setOperation(requestType.getOperationName());
    queryCaseCountRequestType.setEndTime(requestType.getEndTime());
    queryCaseCountRequestType.setBeginTime(requestType.getBeginTime());
    QueryCaseCountResponseType queryCaseCountResponseType = httpWebServiceApiClient.post(true,
        countRecordUrl, queryCaseCountRequestType, QueryCaseCountResponseType.class);
    CountRecordResponseType responseType = new CountRecordResponseType();
    responseType.setRecordedCaseCount(
        Optional.ofNullable(queryCaseCountResponseType).map(QueryCaseCountResponseType::getCount)
            .orElse(0L));

    return responseType;
  }

  public ListRecordResponseType listRecord(ListRecordRequestType requestType) {
    PagedRequestType pagedRequestType = toPagedRequestType(requestType);
    pagedRequestType.setEndTime(requestType.getEndTime());
    pagedRequestType.setBeginTime(requestType.getBeginTime());
    pagedRequestType.setSortingOptions(Collections
        .singletonList(
            new SortingOption(CREATE_TIME_COLUMN_NAME, SortingTypeEnum.DESCENDING.getCode())));

    ListRecordResponseType responseType = new ListRecordResponseType();
    List<ListRecordResponseType.RecordItem> recordItemList = new ArrayList<>();
    responseType.setRecordList(recordItemList);
    String operationType = requestType.getOperationType();
    pagedRequestType.setCategory(MockCategoryType.createEntryPoint(operationType));

    PagedResponseType listResponse = httpWebServiceApiClient.post(true, listRecordUrl,
        pagedRequestType, PagedResponseType.class);

    if (listResponse != null) {
      recordItemList.addAll(listResponse.getRecords().stream()
          .map(arexMocker -> toRecordItem(arexMocker, operationType)).collect(Collectors.toList()));
    }

    QueryCaseCountResponseType countResponse = httpWebServiceApiClient.post(true,
        countRecordUrl, pagedRequestType, QueryCaseCountResponseType.class);

    if (countResponse != null) {
      responseType.setTotalCount(countResponse.getCount());
    }
    return responseType;
  }

  public AggCountRecordResponseType aggCountRecord(CountRecordRequestType requestType) {
    List<ApplicationOperationConfiguration> operationList =
        applicationOperationConfigurationRepository.listBy(requestType.getAppId());

    CountOperationCaseRequestType countOperationCaseRequestType = new CountOperationCaseRequestType();
    countOperationCaseRequestType.setAppId(requestType.getAppId());
    countOperationCaseRequestType.setEndTime(requestType.getEndTime());
    countOperationCaseRequestType.setBeginTime(requestType.getBeginTime());
    CountOperationCaseResponseType response = httpWebServiceApiClient.post(true,
        aggCountRecordUrl, countOperationCaseRequestType,
        CountOperationCaseResponseType.class);

    Map<String, Long> countMap = Optional.ofNullable(response)
        .map(CountOperationCaseResponseType::getCountMap).orElse(new HashMap<>());
    operationList.forEach(operation -> operation
        .setRecordedCaseCount(countMap.getOrDefault(operation.getOperationName(), 0L).intValue()));
    AggCountRecordResponseType responseType = new AggCountRecordResponseType();
    responseType.setOperationList(operationList);
    return responseType;
  }

  private PagedRequestType toPagedRequestType(ListRecordRequestType input) {
    PagedRequestType output = new PagedRequestType();
    output.setAppId(input.getAppId());
    output.setPageIndex(input.getPageIndex());
    output.setOperation(input.getOperationName());
    output.setPageSize(input.getPageSize());
    return output;
  }

  private ListRecordResponseType.RecordItem toRecordItem(AREXMocker input, String operationType) {
    ListRecordResponseType.RecordItem output = new ListRecordResponseType.RecordItem();
    output.setRecordId(input.getRecordId());
    output.setCreateTime(input.getCreationTime());
    output.setOperationType(operationType);
    return output;
  }
}
