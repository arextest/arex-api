package com.arextest.web.model.contract.contracts.record;

import java.util.List;

import lombok.Data;

@Data
public class ListRecordResponseType {
    /**
     * for paging only First-Page will return
     */
    private Long totalCount;

    private List<RecordItem> recordList;

    @Data
    public static class RecordItem {
        private String recordId;
        private Long createTime;
        private String operationType;
    }
}
