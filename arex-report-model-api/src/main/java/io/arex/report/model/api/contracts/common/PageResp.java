package io.arex.report.model.api.contracts.common;

import lombok.Data;


@Data
public class PageResp {
    
    private Integer pageIndex;
    
    private Integer pageSize;
    
    private Integer totalElements;
    
    private Integer totalPages;
}
