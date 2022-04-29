package com.arextest.report.model.api.contracts.common;

import lombok.Data;


@Data
public class PageQuery {
    
    private Integer pageIndex;
    
    private Integer pageSize;
    
    private Boolean queryTotalNum;
}
