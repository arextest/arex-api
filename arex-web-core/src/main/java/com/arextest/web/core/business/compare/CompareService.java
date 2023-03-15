package com.arextest.web.core.business.compare;

import com.arextest.common.utils.CompressionUtils;
import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.CompareResult;
import com.arextest.diff.sdk.CompareSDK;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.ManualReportService;
import com.arextest.web.core.business.util.ListUtils;
import com.arextest.web.core.repository.BatchCompareReportRepository;
import com.arextest.web.core.repository.FSCaseRepository;
import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.contract.contracts.common.NodeEntity;
import com.arextest.web.model.contract.contracts.compare.CaseCompareResponseType;
import com.arextest.web.model.contract.contracts.compare.DiffDetail;
import com.arextest.web.model.contract.contracts.compare.ExceptionMsg;
import com.arextest.web.model.contract.contracts.compare.MsgCombination;
import com.arextest.web.model.contract.contracts.compare.QuickCompareResponseType;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonSummaryConfiguration;
import com.arextest.web.model.dto.filesystem.ComparisonMsgDto;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.manualreport.SaveManualReportCaseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2022/6/30.
 */
@Slf4j
@Component
public class CompareService {

    private static CompareSDK compareSDK = new CompareSDK();

    @Autowired
    ManualReportService manualReportService;

    @Autowired
    FSCaseRepository fsCaseRepository;

    @Autowired
    BatchCompareReportRepository batchCompareReportRepository;

    public QuickCompareResponseType quickCompare(MsgCombination msgCombination) {
        QuickCompareResponseType quickCompareResponseType = new QuickCompareResponseType();
        ComparisonSummaryConfiguration comparisonConfig = msgCombination.getComparisonConfig();
        CompareOptions compareOptions = new CompareOptions();
        if (comparisonConfig != null) {
            compareOptions.putExclusions(comparisonConfig.getExclusionList());
            compareOptions.putInclusions(comparisonConfig.getInclusionList());
            compareOptions.putListSortConfig(comparisonConfig.getListSortMap());
            compareOptions.putReferenceConfig(comparisonConfig.getReferenceMap());
        }
        CompareResult compareResult =
                compareSDK.compare(msgCombination.getBaseMsg(), msgCombination.getTestMsg(), compareOptions);
        quickCompareResponseType.setDiffResultCode(compareResult.getCode());
        quickCompareResponseType.setBaseMsg(compareResult.getProcessedBaseMsg());
        quickCompareResponseType.setTestMsg(compareResult.getProcessedTestMsg());
        if (compareResult.getLogs() != null) {
            List<LogEntity> logs = compareResult.getLogs()
                    .stream()
                    .map(LogEntityMapper.INSTANCE::fromLogEntity)
                    .collect(Collectors.toList());
            quickCompareResponseType.setDiffDetails(getDiffDetails(logs));
        }
        return quickCompareResponseType;
    }

    public QuickCompareResponseType quickCompareCompressMsg(MsgCombination msgCombination) {
        msgCombination.setBaseMsg(
                CompressionUtils.useZstdDecompress(msgCombination.getBaseMsg())
        );
        msgCombination.setTestMsg(
                CompressionUtils.useZstdDecompress(msgCombination.getTestMsg())
        );
        QuickCompareResponseType response = quickCompare(msgCombination);
        response.setBaseMsg(CompressionUtils.useZstdCompress(response.getBaseMsg()));
        response.setTestMsg(CompressionUtils.useZstdCompress(response.getTestMsg()));
        return response;
    }

    @Async("compare-task-executor")
    public void aggCompare(List<MsgCombination> msgCombinations) {
        List<SaveManualReportCaseDto> saveManualReportCaseDtos = Optional.ofNullable(msgCombinations)
                .orElse(new ArrayList<>()).stream().map(item -> {
                    CompareResult compareResult = compareSDK.compare(item.getBaseMsg(), item.getTestMsg());
                    SaveManualReportCaseDto saveManualReportCaseDto = new SaveManualReportCaseDto();
                    saveManualReportCaseDto.setId(item.getCaseId());
                    saveManualReportCaseDto.setBaseMsg(compareResult.getProcessedBaseMsg());
                    saveManualReportCaseDto.setTestMsg(compareResult.getProcessedTestMsg());
                    saveManualReportCaseDto.setLogs(compareResult.getLogs() == null ? null :
                            compareResult.getLogs()
                                    .stream()
                                    .map(LogEntityMapper.INSTANCE::fromLogEntity)
                                    .collect(Collectors.toList()));
                    saveManualReportCaseDto.setDiffResultCode(compareResult.getCode());
                    return saveManualReportCaseDto;
                }).collect(Collectors.toList());
        boolean saveResult = manualReportService.saveManualReportCaseResults(saveManualReportCaseDtos);
        printLogger(msgCombinations, saveResult);

    }

    @Async("compare-task-executor")
    public void sendException(List<ExceptionMsg> exceptionMsgs) {
        List<SaveManualReportCaseDto> saveManualReportCaseDtos =
                Optional.ofNullable(exceptionMsgs).orElse(new ArrayList<>()).stream().map(item -> {
                    CompareResult compareResult =
                            CompareSDK.fromException(item.getBaseMsg(), item.getTestMsg(), item.getRemark());
                    SaveManualReportCaseDto saveManualReportCaseDto = new SaveManualReportCaseDto();
                    saveManualReportCaseDto.setId(item.getCaseId());
                    saveManualReportCaseDto.setBaseMsg(compareResult.getProcessedBaseMsg());
                    saveManualReportCaseDto.setTestMsg(compareResult.getProcessedTestMsg());
                    saveManualReportCaseDto.setLogs(compareResult.getLogs() == null ? null :
                            compareResult.getLogs()
                                    .stream()
                                    .map(LogEntityMapper.INSTANCE::fromLogEntity)
                                    .collect(Collectors.toList()));
                    saveManualReportCaseDto.setDiffResultCode(compareResult.getCode());
                    return saveManualReportCaseDto;
                }).collect(Collectors.toList());
        boolean saveResult = manualReportService.saveManualReportCaseResults(saveManualReportCaseDtos);
        printLogger(exceptionMsgs, saveResult);
    }

    @Deprecated
    public CaseCompareResponseType caseCompare(MsgCombination msgCombination) {
        long start = System.currentTimeMillis();
        CaseCompareResponseType caseCompareResponseType = new CaseCompareResponseType();
        ComparisonSummaryConfiguration comparisonConfig = msgCombination.getComparisonConfig();
        CompareOptions compareOptions = new CompareOptions();
        if (comparisonConfig != null) {
            compareOptions.putExclusions(comparisonConfig.getExclusionList());
            compareOptions.putInclusions(comparisonConfig.getInclusionList());
            compareOptions.putListSortConfig(comparisonConfig.getListSortMap());
            compareOptions.putReferenceConfig(comparisonConfig.getReferenceMap());
        }

        CompareResult compareResult = compareSDK.compare(
                msgCombination.getBaseMsg(),
                msgCombination.getTestMsg(),
                compareOptions);
        caseCompareResponseType.setDiffResultCode(compareResult.getCode());
        caseCompareResponseType.setErrCount(compareResult.getLogs() != null ?
                compareResult.getLogs().size() : 0);

        FSCaseDto fsCaseDto = new FSCaseDto();
        fsCaseDto.setId(msgCombination.getCaseId());
        ComparisonMsgDto comparisonMsgDto = new ComparisonMsgDto();
        comparisonMsgDto.setDiffResultCode(compareResult.getCode());
        comparisonMsgDto.setBaseMsg(compareResult.getProcessedBaseMsg());
        comparisonMsgDto.setTestMsg(compareResult.getProcessedTestMsg());
        if (compareResult.getLogs() != null) {
            List<LogEntity> logs = compareResult.getLogs().stream()
                    .map(LogEntityMapper.INSTANCE::fromLogEntity).collect(Collectors.toList());
            List<DiffDetail> diffDetails = getDiffDetails(logs);
            comparisonMsgDto.setDiffDetails(diffDetails);
        }
        fsCaseDto.setComparisonMsg(comparisonMsgDto);
        fsCaseRepository.updateCase(fsCaseDto);
        caseCompareResponseType.setCostTime(System.currentTimeMillis() - start);

        return caseCompareResponseType;
    }


    public CompareResult batchCompare(String baseMsg, String testMsg, ComparisonSummaryConfiguration comparisonConfig) {
        CompareOptions compareOptions = new CompareOptions();
        if (comparisonConfig != null) {
            compareOptions.putExclusions(comparisonConfig.getExclusionList());
            compareOptions.putInclusions(comparisonConfig.getInclusionList());
            compareOptions.putListSortConfig(comparisonConfig.getListSortMap());
            compareOptions.putReferenceConfig(comparisonConfig.getReferenceMap());
        }
        CompareResult compareResult = compareSDK.compare(
                baseMsg,
                testMsg,
                compareOptions);
        return compareResult;
    }


    private void aggBatchCompareResult(CompareResult compareResult) {
        List<LogEntity> logs = compareResult.getLogs().stream()
                .map(LogEntityMapper.INSTANCE::fromLogEntity).collect(Collectors.toList());
        List<DiffDetail> diffDetails = getDiffDetails(logs);
    }

    public List<DiffDetail> getDiffDetails(List<LogEntity> logs) {

        List<DiffDetail> diffDetails = new ArrayList<>();
        Map<String, Map<Integer, List<LogEntity>>> collect =
                Optional.ofNullable(logs).orElse(new ArrayList<>()).stream()
                        .collect(Collectors.groupingBy(item -> {
                            int leftSize = item.getPathPair().getLeftUnmatchedPath().size();
                            int rightSize = item.getPathPair().getRightUnmatchedPath().size();
                            List<NodeEntity> path = leftSize > rightSize ? item.getPathPair().getLeftUnmatchedPath()
                                    : item.getPathPair().getRightUnmatchedPath();
                            return ListUtils.convertPathToFuzzyPath(path);
                        }, Collectors.groupingBy(item -> item.getPathPair().getUnmatchedType())));

        collect.forEach((k, v) -> {
            for (Integer unmatchedType : v.keySet()) {
                DiffDetail diffDetail = new DiffDetail();
                diffDetail.setPath(k);
                diffDetail.setUnmatchedType(unmatchedType);
                diffDetail.setLogs(v.get(unmatchedType));
                diffDetails.add(diffDetail);
            }
        });
        return diffDetails;
    }

    private <T> void printLogger(List<T> msgs, boolean saveResult) {
        if (!saveResult) {
            List<String> caseIds = Optional.ofNullable(msgs).orElse(new ArrayList<>()).stream().map(item -> {
                if (item instanceof MsgCombination) {
                    MsgCombination msgCombination = (MsgCombination) item;
                    return msgCombination.getCaseId();
                }
                if (item instanceof ExceptionMsg) {
                    ExceptionMsg exceptionMsg = (ExceptionMsg) item;
                    return exceptionMsg.getCaseId();
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            LogUtils.error(LOGGER, "CompareService.save. caseId:{}", caseIds.toString());
        }
    }
}
