package com.arextest.report.model.api.contracts;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class QueryFullLinkMsgRequestType {
    @NotBlank(message = "RecordId cannot be empty")
    private String recordId;
    private String planItemId;
}
