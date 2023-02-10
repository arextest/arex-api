package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.batchcomparereport.UpdateBatchCompareCaseRequestType;
import com.arextest.web.model.dao.mongodb.batchcomparereport.BatchCompareReportCaseCollection;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportCaseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2023/2/7.
 */
@Mapper
public interface BatchCompareReportCaseMapper {

    BatchCompareReportCaseMapper INSTANCE = Mappers.getMapper(BatchCompareReportCaseMapper.class);

    BatchCompareReportCaseCollection daoFromDto(BatchCompareReportCaseDto dto);

    BatchCompareReportCaseDto dtoFromDao(BatchCompareReportCaseCollection dao);

    BatchCompareReportCaseDto dtoFromRequest(UpdateBatchCompareCaseRequestType request);

}
