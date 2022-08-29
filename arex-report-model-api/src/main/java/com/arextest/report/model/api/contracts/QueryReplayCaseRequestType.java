package com.arextest.report.model.api.contracts;

import com.arextest.report.model.api.PagingRequest;
import lombok.Data;


@Data
public class QueryReplayCaseRequestType implements PagingRequest {
    private Integer pageIndex;
    private Integer pageSize;
    private Boolean needTotal;

    
    private String planItemId;

    
    private Integer diffResultCode;
    
    private String keyWord;
}
