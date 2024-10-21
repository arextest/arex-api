package com.arextest.web.core.business;

import com.arextest.web.core.repository.LogsRepository;
import com.arextest.web.model.contract.contracts.QueryLogsRequestType;
import com.arextest.web.model.contract.contracts.common.LogsType;
import com.arextest.web.model.dto.LogsDto;
import com.arextest.web.model.mapper.LogsMapper;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2023/2/10
 */
@Component
public class LogsService {

  @Resource
  private LogsRepository logsRepository;

  public List<LogsType> queryLogs(QueryLogsRequestType request) {
    List<LogsDto> dtos = logsRepository.queryLogs(LogsMapper.INSTANCE.fromRequest(request));
    return dtos.stream().map(LogsMapper.INSTANCE::contractFromDto).collect(Collectors.toList());
  }
}
