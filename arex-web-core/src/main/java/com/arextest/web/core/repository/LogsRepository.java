package com.arextest.web.core.repository;

import java.util.List;

import com.arextest.web.model.dto.LogsDto;
import com.arextest.web.model.params.QueryLogsParam;

/**
 * @author b_yu
 * @since 2023/2/10
 */
public interface LogsRepository extends RepositoryProvider {
    List<LogsDto> queryLogs(QueryLogsParam param);
}
