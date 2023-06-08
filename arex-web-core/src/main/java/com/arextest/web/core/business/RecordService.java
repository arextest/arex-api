package com.arextest.web.core.business;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.model.replay.PagedRequestType;
import com.arextest.model.replay.PagedResponseType;
import com.arextest.model.replay.QueryCaseCountRequestType;
import com.arextest.model.replay.QueryCaseCountResponseType;
import com.arextest.model.replay.SortingOption;
import com.arextest.model.replay.SortingTypeEnum;
import com.arextest.web.common.HttpUtils;
import com.arextest.web.model.contract.contracts.record.CountRecordRequestType;
import com.arextest.web.model.contract.contracts.record.CountRecordResponseType;
import com.arextest.web.model.contract.contracts.record.ListRecordRequestType;
import com.arextest.web.model.contract.contracts.record.ListRecordResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecordService {

    @Value("${arex.storage.countRecord.url}")
    private String countRecordUrl;
    @Value(("${arex.storage.listRecord.url}"))
    private String listRecordUrl;

    private static final String CREATE_TIME_COLUMN_NAME = "creationTime";

    public CountRecordResponseType countRecord(CountRecordRequestType requestType) {
        QueryCaseCountRequestType queryCaseCountRequestType = new QueryCaseCountRequestType();
        queryCaseCountRequestType.setAppId(requestType.getAppId());
        queryCaseCountRequestType.setOperation(requestType.getOperationName());
        queryCaseCountRequestType.setEndTime(requestType.getEndTime());
        queryCaseCountRequestType.setBeginTime(requestType.getBeginTime());
        ResponseEntity<QueryCaseCountResponseType> response =
                HttpUtils.post(countRecordUrl, queryCaseCountRequestType, QueryCaseCountResponseType.class);
        CountRecordResponseType responseType = new CountRecordResponseType();
        responseType.setRecordedCaseCount(
                Optional.ofNullable(response.getBody()).map(QueryCaseCountResponseType::getCount)
                        .orElse(0L));

        return responseType;
    }

    public ListRecordResponseType listRecord(ListRecordRequestType requestType) {
        PagedRequestType pagedRequestType = toPagedRequestType(requestType);
        pagedRequestType.setEndTime(requestType.getEndTime());
        pagedRequestType.setBeginTime(requestType.getBeginTime());
        pagedRequestType.setSortingOptions(Collections.singletonList(
                new SortingOption(CREATE_TIME_COLUMN_NAME, SortingTypeEnum.DESCENDING.getCode())));

        ListRecordResponseType responseType = new ListRecordResponseType();
        List<ListRecordResponseType.RecordItem> recordItemList = new ArrayList<>();
        responseType.setRecordList(recordItemList);

        ResponseEntity<PagedResponseType> listResponse;
        for (String operationType : requestType.getOperationTypes()) {
            pagedRequestType.setCategory(MockCategoryType.createEntryPoint(operationType));
            listResponse = HttpUtils.post(listRecordUrl, pagedRequestType, PagedResponseType.class);
            if (listResponse != null && listResponse.getBody() != null) {
                recordItemList.addAll(listResponse.getBody().getRecords()
                        .stream()
                        .map(arexMocker -> toRecordItem(arexMocker, operationType))
                        .collect(Collectors.toList()));
            }
        }

        pagedRequestType.setCategory(null);
        ResponseEntity<QueryCaseCountResponseType> countResponse =
                HttpUtils.post(countRecordUrl, pagedRequestType, QueryCaseCountResponseType.class);
        if (countResponse != null && countResponse.getBody() != null) {
            responseType.setTotalCount(countResponse.getBody().getCount());
        }
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
