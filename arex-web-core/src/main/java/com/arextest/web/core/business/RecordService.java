package com.arextest.web.core.business;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.replay.CountRecordCaseResponseType;
import com.arextest.model.replay.ListRecordCaseRequestType;
import com.arextest.model.replay.ListRecordCaseResponseType;
import com.arextest.web.common.HttpUtils;
import com.arextest.web.model.contract.contracts.record.CountRecordResponseType;
import com.arextest.web.model.contract.contracts.record.ListRecordRequestType;
import com.arextest.web.model.contract.contracts.record.ListRecordResponseType;
import com.arextest.web.model.contract.contracts.record.RecordItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecordService {

    @Value("${arex.storage.countRecord.url}")
    private String countRecordUrl;
    @Value(("${arex.storage.listRecord.url}"))
    private String listRecordUrl;

    public CountRecordResponseType countRecord(String appId) {
        ResponseEntity<CountRecordCaseResponseType> response =
                HttpUtils.get(countRecordUrl + appId, CountRecordCaseResponseType.class);
        CountRecordResponseType responseType = new CountRecordResponseType();
        responseType.setRecordedCaseCount(
                Optional.ofNullable(response.getBody()).map(CountRecordCaseResponseType::getRecordedCaseCount)
                        .orElse(0L));

        return responseType;
    }

    public ListRecordResponseType listRecord(ListRecordRequestType requestType) {
        ResponseEntity<ListRecordCaseResponseType> response = HttpUtils.post(
                listRecordUrl, toListRecordCaseRequestType(requestType), ListRecordCaseResponseType.class);

        ListRecordResponseType responseType = new ListRecordResponseType();
        if (response != null && response.getBody() != null) {
            responseType.setTotalCount(response.getBody().getTotalCount());
            responseType.setRecordList(
                    response.getBody().getRecordList().stream().map(this::toRecordItem).collect(Collectors.toList()));
        }
        return responseType;
    }

    private ListRecordCaseRequestType toListRecordCaseRequestType(ListRecordRequestType input) {
        ListRecordCaseRequestType output = new ListRecordCaseRequestType();
        output.setAppId(input.getAppId());
        output.setPageIndex(input.getPageIndex());
        output.setOperationType(input.getOperationType());
        output.setOperationName(input.getOperationName());
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
