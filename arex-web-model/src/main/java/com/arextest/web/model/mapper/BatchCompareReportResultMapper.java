package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.batchcomparereport.BatchCompareReportResultCollection;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2023/2/9.
 */
@Mapper
public interface BatchCompareReportResultMapper {
    BatchCompareReportResultMapper INSTANCE = Mappers.getMapper(BatchCompareReportResultMapper.class);

    BatchCompareReportResultCollection daoFromDto(BatchCompareReportResultDto dto);

    BatchCompareReportResultDto dtoFromDao(BatchCompareReportResultCollection dao);
}
