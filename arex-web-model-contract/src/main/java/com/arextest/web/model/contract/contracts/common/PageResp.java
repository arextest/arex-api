package com.arextest.web.model.contract.contracts.common;

import lombok.Data;


@Data
public class PageResp {
    
    private Integer pageIndex;
    
    private Integer pageSize;
    
    private Integer totalElements;
    
    private Integer totalPages;
}
