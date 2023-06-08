package com.arextest.web.model.contract.contracts.record;

import lombok.Data;

import java.util.List;

@Data
public class ListRecordResponseType {
    /**
     * for paging
     * only First-Page will return
     */
    private Long totalCount;

    /**
     * simple version of
     * @see com.arextest.model.mock.AREXMocker
     */
    private List<RecordItem> recordList;


    @Data
    public static class RecordItem {
        private String recordId;
        private Long createTime;
        private String operationType;
    }
}
