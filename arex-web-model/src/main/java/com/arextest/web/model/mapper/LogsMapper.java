package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.QueryLogsRequestType;
import com.arextest.web.model.contract.contracts.common.LogsType;
import com.arextest.web.model.dao.mongodb.LogsCollection;
import com.arextest.web.model.dto.LogsDto;
import com.arextest.web.model.params.QueryLogsParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author b_yu
 * @since 2023/2/10
 */
@Mapper
public interface LogsMapper {
    LogsMapper INSTANCE = Mappers.getMapper(LogsMapper.class);

    LogsDto dtoFromDao(LogsCollection dao);

    LogsType contractFromDto(LogsDto dto);

    QueryLogsParam fromRequest(QueryLogsRequestType request);
}
