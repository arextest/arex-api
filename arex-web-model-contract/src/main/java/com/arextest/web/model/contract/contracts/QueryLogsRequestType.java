package com.arextest.web.model.contract.contracts;

import java.util.Map;

import com.arextest.web.model.contract.PagingRequest;

import lombok.Data;

/**
 * @author b_yu
 * @since 2023/2/10
 */
@Data
public class QueryLogsRequestType implements PagingRequest {
    private Integer pageIndex;
    private Integer pageSize;
    private Boolean needTotal;

    private String previousId;
    private String level;
    private Long startTime;
    private Long endTime;
    private Map<String, String> tags;
}
