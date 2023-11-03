package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.LogsType;
import java.util.List;
import lombok.Data;

/**
 * @author b_yu
 * @since 2023/2/10
 */
@Data
public class QueryLogsResponseType {

  private List<LogsType> logs;
}
