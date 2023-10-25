package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

@Data
public class PageQuery {

    private Integer pageIndex;

    private Integer pageSize;

    private Boolean queryTotalNum;
}
