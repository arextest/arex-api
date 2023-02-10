package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingRequest;
import lombok.Data;

import java.util.Map;

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
