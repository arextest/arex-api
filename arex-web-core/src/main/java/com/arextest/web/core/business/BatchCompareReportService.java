package com.arextest.web.core.business;

import com.arextest.diff.model.CompareResult;
import com.arextest.web.core.business.compare.CompareService;
import com.arextest.web.core.business.compare.LogEntityMapper;
import com.arextest.web.core.repository.BatchCompareReportRepository;
import com.arextest.web.core.repository.BatchCompareReportResultRepository;
import com.arextest.web.core.repository.BatchCompareReportStatisticsRepository;
import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareInterfaceProcess;
import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareReportRequestType;
import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareSummaryItem;
import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareProgressRequestType;
import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareSummaryRequestType;
import com.arextest.web.model.contract.contracts.batchcomparereport.UpdateBatchCompareCaseRequestType;
import com.arextest.web.model.contract.contracts.common.BatchCompareCaseStatusType;
import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.contract.contracts.compare.DiffDetail;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportCaseDto;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportResultDto;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportStatisticsDto;
import com.arextest.web.model.enums.DiffResultCode;
import com.arextest.web.model.mapper.BatchCompareReportCaseMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2023/2/7.
 */
@Slf4j
@Component
public class BatchCompareReportService {

    @Autowired
    CompareService compareService;
    @Autowired
    BatchCompareReportRepository batchCompareReportRepository;
    @Autowired
    BatchCompareReportStatisticsRepository batchCompareReportStatisticsRepository;
    @Autowired
    BatchCompareReportResultRepository batchCompareReportResultRepository;

    public boolean initBatchCompareReport(BatchCompareReportRequestType request) {

        List<BatchCompareReportCaseDto> batchCompareReportCaseDtoList = new ArrayList<>();
        String planId = request.getPlanId();
        long createTime = System.currentTimeMillis();
        List<BatchCompareReportRequestType.BatchCompareCase> batchCompareCaseList = request.getBatchCompareCaseList();
        for (BatchCompareReportRequestType.BatchCompareCase batchCompareCase : batchCompareCaseList) {
            BatchCompareReportCaseDto batchCompareReportCaseDto = new BatchCompareReportCaseDto();
            batchCompareReportCaseDto.setPlanId(planId);
            batchCompareReportCaseDto.setInterfaceId(batchCompareCase.getInterfaceId());
            batchCompareReportCaseDto.setCaseId(batchCompareCase.getCaseId());
            batchCompareReportCaseDto.setInterfaceName(batchCompareCase.getInterfaceName());
            batchCompareReportCaseDto.setCaseName(batchCompareCase.getCaseName());
            batchCompareReportCaseDto.setDataChangeCreateTime(createTime);
            batchCompareReportCaseDtoList.add(batchCompareReportCaseDto);
        }
        return batchCompareReportRepository.insertAll(batchCompareReportCaseDtoList);
    }

    public boolean updateBatchCompareCase(UpdateBatchCompareCaseRequestType request) {
        BatchCompareReportCaseDto dto = BatchCompareReportCaseMapper.INSTANCE.dtoFromRequest(request);
        dto.setStatus(BatchCompareCaseStatusType.WAIT_COMPARE);
        batchCompareReportRepository.updateBatchCompareCase(dto);
        this.batchCompareAndAgg(request);
        return true;
    }


    @Async("compare-task-executor")
    public void batchCompareAndAgg(UpdateBatchCompareCaseRequestType request) {
        String planId = request.getPlanId();
        String interfaceId = request.getInterfaceId();
        String caseId = request.getCaseId();

        BatchCompareReportCaseDto dto = new BatchCompareReportCaseDto();
        dto.setPlanId(planId);
        dto.setInterfaceId(interfaceId);
        dto.setCaseId(caseId);
        try {
            if (StringUtils.isNotEmpty(request.getExceptionMsg())) {
                dto.setStatus(BatchCompareCaseStatusType.EXCEPTION);
                dto.setExceptionMsg(request.getExceptionMsg());
            } else {
                CompareResult compareResult = compareService.batchCompare(
                        request.getBaseMsg(), request.getTestMsg(), request.getComparisonConfig());
                int code = compareResult.getCode();
                dto.setStatus(convertDiffResultCode(code));
                dto.setProcessedBaseMsg(compareResult.getProcessedBaseMsg());
                dto.setProcessedTestMsg(compareResult.getProcessedTestMsg());
                if (code == DiffResultCode.COMPARED_INTERNAL_EXCEPTION) {
                    dto.setExceptionMsg(compareResult.getMessage());
                } else if (code == DiffResultCode.COMPARED_WITH_DIFFERENCE) {
                    List<LogEntity> logs = compareResult.getLogs().stream()
                            .map(LogEntityMapper.INSTANCE::fromLogEntity).collect(Collectors.toList());
                    List<DiffDetail> diffDetails = compareService.getDiffDetails(logs);
                    // 更新错误表和统计表
                    this.updateBatchCompareReportStatistics(planId, interfaceId, caseId, diffDetails);
                }
            }
        } catch (Throwable throwable) {
            dto.setStatus(BatchCompareCaseStatusType.EXCEPTION);
            dto.setExceptionMsg(throwable.getMessage());
            LOGGER.error(String.format("batchCompare exception. planId:%s, caseId:%s", planId, caseId),
                    throwable);
        }
        batchCompareReportRepository.updateBatchCompareCase(dto);
    }

    public List<BatchCompareInterfaceProcess> queryBatchCompareProgress(QueryBatchCompareProgressRequestType request) {
        String planId = request.getPlanId();
        return batchCompareReportRepository.queryBatchCompareProgress(planId);
    }

    public List<BatchCompareSummaryItem> queryBatchCompareSummary(QueryBatchCompareSummaryRequestType request) {
        String planId = request.getPlanId();
        String interfaceId = request.getInterfaceId();
        return batchCompareReportStatisticsRepository.queryBatchCompareSummary(planId, interfaceId);
    }


    private void updateBatchCompareReportStatistics(String planId, String interfaceId, String caseId,
                                                    List<DiffDetail> diffDetails) {
        if (CollectionUtils.isEmpty(diffDetails)) {
            return;
        }

        Map<BatchCompareCardInfo, String> batchCompareCardInfoToIdMap =
                saveBatchCompareReportResult(planId, interfaceId, caseId, diffDetails);

        for (Map.Entry<BatchCompareCardInfo, String> batchCompareCardInfoToIdItem : batchCompareCardInfoToIdMap.entrySet()) {
            BatchCompareCardInfo batchCompareCardInfo = batchCompareCardInfoToIdItem.getKey();
            String logId = batchCompareCardInfoToIdItem.getValue();
            BatchCompareReportStatisticsDto dto = new BatchCompareReportStatisticsDto();
            dto.setPlanId(planId);
            dto.setInterfaceId(interfaceId);
            dto.setUnMatchedType(batchCompareCardInfo.getUnmatchedType());
            dto.setFuzzyPath(batchCompareCardInfo.getFuzzyPath());
            dto.setErrorCount(batchCompareCardInfo.getErrorCount());
            dto.setLogEntity(batchCompareCardInfo.getLogEntity());
            dto.setLogId(logId);
            batchCompareReportStatisticsRepository.updateBatchCompareReportStatistics(dto);
        }
    }

    private Map<BatchCompareCardInfo, String> saveBatchCompareReportResult(String planId, String interfaceId,
                                                                           String caseId,
                                                                           List<DiffDetail> diffDetails) {
        Map<BatchCompareCardInfo, Integer> diffDetailToIndex = new HashMap<>();
        List<BatchCompareReportResultDto> batchCompareReportResultDtos = new ArrayList<>();
        for (int i = 0; i < diffDetails.size(); i++) {
            DiffDetail diffDetail = diffDetails.get(i);
            List<LogEntity> logs = diffDetail.getLogs();
            if (CollectionUtils.isNotEmpty(logs)) {
                for (int j = 0; j < logs.size(); j++) {

                    int unMatchedType = diffDetail.getUnmatchedType();
                    String path = diffDetail.getPath();
                    LogEntity logEntity = logs.get(j);

                    BatchCompareReportResultDto batchCompareReportResultDto = new BatchCompareReportResultDto();
                    batchCompareReportResultDto.setPlanId(planId);
                    batchCompareReportResultDto.setInterfaceId(interfaceId);
                    batchCompareReportResultDto.setCaseId(caseId);
                    batchCompareReportResultDto.setUnMatchedType(unMatchedType);
                    batchCompareReportResultDto.setFuzzyPath(path);
                    batchCompareReportResultDto.setLogEntity(logEntity);
                    batchCompareReportResultDtos.add(batchCompareReportResultDto);

                    if (j == 0) {
                        BatchCompareCardInfo batchCompareCardInfo =
                                new BatchCompareCardInfo(unMatchedType, path, logs.size(), logEntity);
                        diffDetailToIndex.put(batchCompareCardInfo, batchCompareReportResultDtos.size() - 1);
                    }
                }
            }
        }

        Map<BatchCompareCardInfo, String> diffDetailToId = new HashMap<>();
        List<String> logIds = batchCompareReportResultRepository.insertAll(batchCompareReportResultDtos);
        for (Map.Entry<BatchCompareCardInfo, Integer> entry : diffDetailToIndex.entrySet()) {
            diffDetailToId.put(entry.getKey(), logIds.get(entry.getValue()));
        }
        return diffDetailToId;
    }

    private int convertDiffResultCode(int code) {
        int result = 0;
        switch (code) {
            case DiffResultCode.COMPARED_WITHOUT_DIFFERENCE:
                result = BatchCompareCaseStatusType.SUCCESS;
                break;
            case DiffResultCode.COMPARED_WITH_DIFFERENCE:
                result = BatchCompareCaseStatusType.ERROR;
                break;
            default:
                result = BatchCompareCaseStatusType.EXCEPTION;
                break;
        }
        return result;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BatchCompareCardInfo {
        private int unmatchedType;
        private String fuzzyPath;
        private int errorCount;
        private LogEntity logEntity;
    }

}
