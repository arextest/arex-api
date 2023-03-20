package com.arextest.web.core.business;

import com.arextest.common.utils.CompressionUtils;
import com.arextest.diff.model.CompareResult;
import com.arextest.web.common.ZstdUtils;
import com.arextest.web.core.business.compare.CompareService;
import com.arextest.web.core.business.compare.LogEntityMapper;
import com.arextest.web.core.repository.BatchCompareReportRepository;
import com.arextest.web.core.repository.BatchCompareReportResultRepository;
import com.arextest.web.core.repository.BatchCompareReportStatisticsRepository;
import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareInterfaceProcess;
import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareMoreItem;
import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareReportRequestType;
import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareSummaryItem;
import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareCaseMsgWithDiffResponseType;
import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareProgressRequestType;
import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareSummaryRequestType;
import com.arextest.web.model.contract.contracts.batchcomparereport.QueryMoreDiffInSameCardRequestType;
import com.arextest.web.model.contract.contracts.batchcomparereport.QueryMoreDiffInSameCardResponseType;
import com.arextest.web.model.contract.contracts.batchcomparereport.UpdateBatchCompareCaseRequestType;
import com.arextest.web.model.contract.contracts.common.BatchCompareCaseStatusType;
import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.contract.contracts.compare.DiffDetail;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportCaseDto;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportResultDto;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportStatisticsDto;
import com.arextest.web.model.enums.DiffResultCode;
import com.arextest.web.model.mapper.BatchCompareReportCaseMapper;
import com.arextest.web.model.mapper.BatchCompareReportResultMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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

    @Autowired
    MsgShowService msgShowService;

    @Resource(name = "compare-task-executor")
    ThreadPoolTaskExecutor executor;

    public InitBatchCompareReportResult initBatchCompareReport(BatchCompareReportRequestType request) {

        List<BatchCompareReportCaseDto> batchCompareReportCaseDtoList = new ArrayList<>();
        String planId = UUID.randomUUID().toString();
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
        return batchCompareReportRepository.insertAll(batchCompareReportCaseDtoList) ?
                new InitBatchCompareReportResult(planId) : null;
    }

    public boolean updateBatchCompareCase(UpdateBatchCompareCaseRequestType request) {
        BatchCompareReportCaseDto dto = BatchCompareReportCaseMapper.INSTANCE.dtoFromRequest(request);
        dto.setStatus(BatchCompareCaseStatusType.WAIT_COMPARE);
        batchCompareReportRepository.updateBatchCompareCase(dto);
        CompletableFuture.runAsync(() -> this.batchCompareAndAgg(request), executor);
        return true;
    }

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
                String baseMsg = ZstdUtils.uncompressString(request.getBaseMsg());
                String testMsg = ZstdUtils.uncompressString(request.getTestMsg());
                CompareResult compareResult = compareService.batchCompare(
                        baseMsg, testMsg, request.getComparisonConfig());
                int code = compareResult.getCode();
                dto.setStatus(convertDiffResultCode(code));
                dto.setProcessedBaseMsg(ZstdUtils.compressString(compareResult.getProcessedBaseMsg()));
                dto.setProcessedTestMsg(ZstdUtils.compressString(compareResult.getProcessedTestMsg()));
                if (code == DiffResultCode.COMPARED_INTERNAL_EXCEPTION) {
                    dto.setExceptionMsg(compareResult.getMessage());
                } else if (code == DiffResultCode.COMPARED_WITH_DIFFERENCE) {
                    List<LogEntity> logs = compareResult.getLogs().stream()
                            .map(LogEntityMapper.INSTANCE::fromLogEntity).collect(Collectors.toList());
                    List<DiffDetail> diffDetails = compareService.getDiffDetails(logs);
                    // update BatchCompareReportResult and BatchCompareReportStatistics
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

    public QueryBatchCompareCaseMsgWithDiffResponseType queryBatchCompareCaseMsgWithDiff(String logId) {
        QueryBatchCompareCaseMsgWithDiffResponseType response =
                new QueryBatchCompareCaseMsgWithDiffResponseType();
        BatchCompareReportResultDto batchCompareReportResultDto = batchCompareReportResultRepository.findById(logId);
        if (batchCompareReportResultDto == null) {
            return null;
        }
        String planId = batchCompareReportResultDto.getPlanId();
        String interfaceId = batchCompareReportResultDto.getInterfaceId();
        String caseId = batchCompareReportResultDto.getCaseId();
        LogEntity logEntity = batchCompareReportResultDto.getLogEntity();
        BatchCompareReportCaseDto batchCompareReportCaseDto =
                batchCompareReportRepository.findById(planId, interfaceId, caseId);
        if (batchCompareReportCaseDto == null) {
            return null;
        }
        String processedBaseMsg = ZstdUtils.uncompressString(
                batchCompareReportCaseDto.getProcessedBaseMsg());
        String processedTestMsg = ZstdUtils.uncompressString(
                batchCompareReportCaseDto.getProcessedTestMsg());
        if (processedBaseMsg != null && processedTestMsg != null) {
            MutablePair<Object, Object> objectObjectMutablePair =
                    msgShowService.produceNewObjectFromOriginal(
                            processedBaseMsg,
                            processedTestMsg,
                            Collections.singletonList(logEntity));
            processedBaseMsg = objectObjectMutablePair.getLeft().toString();
            processedTestMsg = objectObjectMutablePair.getRight().toString();
        }
        response.setBaseMsg(processedBaseMsg);
        response.setTestMsg(processedTestMsg);
        response.setLogEntity(logEntity);
        return response;
    }

    public QueryMoreDiffInSameCardResponseType queryMoreDiffInSameCard(QueryMoreDiffInSameCardRequestType request) {
        QueryMoreDiffInSameCardResponseType response = new QueryMoreDiffInSameCardResponseType();
        BatchCompareReportResultDto dtos =
                BatchCompareReportResultMapper.INSTANCE.dtoFromRequest(request);
        long total = batchCompareReportResultRepository.countAll(dtos);
        List<BatchCompareReportResultDto> batchCompareReportResultDtos =
                batchCompareReportResultRepository.queryAllByPage(dtos, request.getPage(), request.getPageSize());
        List<BatchCompareMoreItem> diffs = batchCompareReportResultDtos
                .stream()
                .map(BatchCompareReportResultMapper.INSTANCE::itemFromDto)
                .collect(Collectors.toList());
        response.setTotal(total);
        response.setDiffs(diffs);
        return response;
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
            dto.setCaseId(caseId);
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class InitBatchCompareReportResult {
        private String planId;
    }

}
