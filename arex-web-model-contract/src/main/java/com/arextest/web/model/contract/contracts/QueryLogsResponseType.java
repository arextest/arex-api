package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.LogsType;

import lombok.Data;

/**
 * @author b_yu
 * @since 2023/2/10
 */
@Data
public class QueryLogsResponseType {
    private List<LogsType> logs;
}
