package com.arextest.web.model.contract.contracts.batchcomparereport;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by rchen9 on 2023/2/14.
 */
@Data
public class QueryMoreDiffInSameCardRequestType {
    @NotBlank(message = "planId cannot be empty")
    private String planId;
    @NotBlank(message = "interfaceId cannot be empty")
    private String interfaceId;
    private int unMatchedType;
    private String fuzzyPath;

    private int page;
    private int pageSize;
}
