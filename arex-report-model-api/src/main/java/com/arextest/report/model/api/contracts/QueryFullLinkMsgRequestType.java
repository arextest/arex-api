package com.arextest.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryFullLinkMsgRequestType {
    private String recordId;
    private String planItemId;
}
