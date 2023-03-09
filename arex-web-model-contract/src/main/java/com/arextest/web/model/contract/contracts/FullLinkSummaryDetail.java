package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/3/8.
 */
@Data
public class FullLinkSummaryDetail {

    private String categoryName;

    private List<FullLinkSummaryDetailInfo> detailInfoList;

    @Data
    public static class FullLinkSummaryDetailInfo {
        /**
         * @see com.arextest.web.model.enums.DiffResultCode
         */
        private int code;

        private int count;

    }
}
