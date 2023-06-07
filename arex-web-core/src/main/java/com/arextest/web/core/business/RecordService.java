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
import com.arextest.web.common.TimeUtils;
import com.arextest.web.model.contract.contracts.record.CountRecordResponseType;
import com.arextest.web.model.contract.contracts.record.ListRecordRequestType;
import com.arextest.web.model.contract.contracts.record.ListRecordResponseType;
import com.arextest.web.model.contract.contracts.record.RecordItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    public CountRecordResponseType countRecord(String appId) {
        QueryCaseCountRequestType requestType = new QueryCaseCountRequestType();
        requestType.setAppId(appId);
        requestType.setEndTime(System.currentTimeMillis());
        requestType.setBeginTime(System.currentTimeMillis() - TimeUtils.ONE_DAY_MILL);
        ResponseEntity<QueryCaseCountResponseType> response =
                HttpUtils.post(countRecordUrl, requestType, QueryCaseCountResponseType.class);
        CountRecordResponseType responseType = new CountRecordResponseType();
        responseType.setRecordedCaseCount(
                Optional.ofNullable(response.getBody()).map(QueryCaseCountResponseType::getCount)
                        .orElse(0L));

        return responseType;
    }

    public ListRecordResponseType listRecord(ListRecordRequestType requestType) {
        PagedRequestType pagedRequestType = toPagedRequestType(requestType);
        pagedRequestType.setEndTime(System.currentTimeMillis());
        pagedRequestType.setBeginTime(System.currentTimeMillis() - TimeUtils.ONE_DAY_MILL);
        pagedRequestType.setSortingOptions(Collections.singletonList(
                new SortingOption(CREATE_TIME_COLUMN_NAME, SortingTypeEnum.DESCENDING.getCode())));

        ResponseEntity<PagedResponseType> listResponse = HttpUtils.post(
                listRecordUrl, pagedRequestType, PagedResponseType.class);
        ListRecordResponseType responseType = new ListRecordResponseType();
        if (listResponse != null && listResponse.getBody() != null) {
            responseType.setRecordList(
                    listResponse.getBody().getRecords().stream().map(this::toRecordItem).collect(Collectors.toList()));
        }

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
        output.setCategory(MockCategoryType.create(input.getOperationType()));
        output.setOperation(input.getOperationName());
        output.setPageSize(input.getPageSize());
        return output;
    }

    private RecordItem toRecordItem(AREXMocker input) {
        RecordItem output = new RecordItem();
        output.setRecordId(input.getRecordId());
        output.setCreateTime(input.getCreationTime());
        return output;
    }
}
