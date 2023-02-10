package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.LogsType;
import lombok.Data;

import java.util.List;

/**
 * @author b_yu
 * @since 2023/2/10
 */
@Data
public class QueryLogsResponseType {
    private List<LogsType> logs;
}
