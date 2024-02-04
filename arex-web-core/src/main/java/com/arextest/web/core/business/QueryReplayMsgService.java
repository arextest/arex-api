package com.arextest.web.core.business;

import com.arextest.common.context.ArexContext;
import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.util.JsonUtils;
import com.arextest.web.core.business.util.ListUtils;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.model.contract.contracts.CompareResultDetail;
import com.arextest.web.model.contract.contracts.DownloadReplayMsgRequestType;
import com.arextest.web.model.contract.contracts.FullLinkInfoItem;
import com.arextest.web.model.contract.contracts.QueryDiffMsgByIdResponseType;
import com.arextest.web.model.contract.contracts.QueryFullLinkInfoResponseType;
import com.arextest.web.model.contract.contracts.QueryFullLinkMsgRequestType;
import com.arextest.web.model.contract.contracts.QueryFullLinkMsgResponseType;
import com.arextest.web.model.contract.contracts.QueryLogEntityRequestTye;
import com.arextest.web.model.contract.contracts.QueryLogEntityResponseType;
import com.arextest.web.model.contract.contracts.QueryReplayMsgRequestType;
import com.arextest.web.model.contract.contracts.QueryReplayMsgResponseType;
import com.arextest.web.model.contract.contracts.common.CompareResult;
import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.contract.contracts.common.NodeEntity;
import com.arextest.web.model.contract.contracts.common.UnmatchedPairEntity;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.iosummary.UnmatchedCategory;
import com.arextest.web.model.enums.DiffResultCode;
import com.arextest.web.model.mapper.CompareResultMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueryReplayMsgService {

  private static final int BIG_MESSAGE_THRESHOLD = 5 * 1024 * 1024;
  @Resource
  private ReplayCompareResultRepository replayCompareResultRepository;
  @Resource
  private ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;
  @Resource
  private AppContractRepository appContractRepository;

  public QueryReplayMsgResponseType queryReplayMsg(QueryReplayMsgRequestType request) {
    QueryReplayMsgResponseType response = new QueryReplayMsgResponseType();
    CompareResultDto dto = replayCompareResultRepository.queryCompareResultsById(request.getId());
    if (dto == null) {
      return response;
    }

    if (Boolean.FALSE.equals(ArexContext.getContext().getPassAuth())) {
      JsonUtils.downgrade(dto);
      response.setDesensitized(true);
    }

    String baseMsg = dto.getBaseMsg();
    String testMsg = dto.getTestMsg();

    String tempBaseMsg = baseMsg != null ? baseMsg : "";
    String tempTestMsg = testMsg != null ? testMsg : "";
    if (tempBaseMsg.length() > BIG_MESSAGE_THRESHOLD) {
      response.setBaseMsgDownload(true);
    } else {
      response.setBaseMsg(baseMsg);
    }
    if (tempTestMsg.length() > BIG_MESSAGE_THRESHOLD) {
      response.setTestMsgDownload(true);
    } else {
      response.setTestMsg(testMsg);
    }
    response.setDiffResultCode(dto.getDiffResultCode());
    if (Objects.equals(dto.getDiffResultCode(), DiffResultCode.COMPARED_INTERNAL_EXCEPTION)) {
      response.setLogs(dto.getLogs());
    }
    return response;
  }

  public void downloadReplayMsg(DownloadReplayMsgRequestType request,
      HttpServletResponse response) {
    CompareResultDto dto = replayCompareResultRepository.queryCompareResultsById(request.getId());
    String fileName = null;
    String msg = null;
    if (request.isBaseMsgDownload()) {
      msg = dto.getBaseMsg();
      fileName = "baseMsg.txt";
    } else {
      msg = dto.getTestMsg();
      fileName = "testMsg.txt";
    }
    response.setHeader("Content-disposition", "attachment; filename=" + fileName);
    InputStream in = null;
    OutputStream out = null;

    try {
      in = new ByteArrayInputStream(msg.getBytes());
      int len = 0;
      byte[] buffer = new byte[1024];
      out = response.getOutputStream();
      while ((len = in.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }
    } catch (IOException e) {
      LogUtils.error(LOGGER, "downloadReplayMsg", e);
    } finally {
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        LogUtils.error(LOGGER, "downloadReplayMsg", e);
      }
    }
  }

  public QueryFullLinkMsgResponseType queryFullLinkMsg(QueryFullLinkMsgRequestType request) {
    QueryFullLinkMsgResponseType response = new QueryFullLinkMsgResponseType();
    List<CompareResultDto> dtos =
        replayCompareResultRepository.queryCompareResultsByRecordId(request.getPlanItemId(),
            request.getRecordId());
    if (dtos == null) {
      return response;
    }

    if (Boolean.FALSE.equals(ArexContext.getContext().getPassAuth())) {
      dtos.forEach(JsonUtils::downgrade);
      response.setDesensitized(true);
    }
    List<CompareResult> compareResults = dtos.stream()
        .map(CompareResultMapper.INSTANCE::contractFromDtoLogsLimitDisplay)
        .collect(Collectors.toList());
    response.setCompareResults(compareResults);
    return response;
  }

  public QueryFullLinkInfoResponseType queryFullLinkInfo(String planItemId, String recordId) {
    QueryFullLinkInfoResponseType response = new QueryFullLinkInfoResponseType();
    List<CompareResultDto> dtos = replayCompareResultRepository.queryCompareResultsByRecordId(
        planItemId, recordId);
    dtos.sort(Comparator.comparingLong(CompareResultDto::getReplayTime)
        .thenComparingLong(CompareResultDto::getRecordTime));

    if (CollectionUtils.isNotEmpty(dtos)) {
      // judge entrance type by operationId
      Set<String> entranceCategoryNames = new HashSet<>();
      CompareResultDto compareResultDto = dtos.get(0);
      String operationId = compareResultDto.getOperationId();
      ApplicationOperationConfiguration applicationOperationConfiguration =
          applicationOperationConfigurationRepository.listByOperationId(operationId);
      if (applicationOperationConfiguration != null) {
        if (CollectionUtils.isNotEmpty(applicationOperationConfiguration.getOperationTypes())) {
          entranceCategoryNames = applicationOperationConfiguration.getOperationTypes();
        }
        entranceCategoryNames.add(applicationOperationConfiguration.getOperationType());
      }

      FullLinkInfoItem entrance = new FullLinkInfoItem();
      List<FullLinkInfoItem> itemList = new ArrayList<>();
      for (CompareResultDto dto : dtos) {
        if (entranceCategoryNames.contains(dto.getCategoryName())) {
          entrance.setId(dto.getId());
          entrance.setCategoryName(dto.getCategoryName());
          entrance.setOperationName(dto.getOperationName());
          entrance.setInstanceId(dto.getInstanceId());
          entrance.setCode(computeItemStatus(dto));
        } else {
          FullLinkInfoItem dependencyItem = new FullLinkInfoItem();
          dependencyItem.setId(dto.getId());
          dependencyItem.setCategoryName(dto.getCategoryName());
          dependencyItem.setOperationName(dto.getOperationName());
          dependencyItem.setInstanceId(dto.getInstanceId());
          dependencyItem.setCode(computeItemStatus(dto));
          dependencyItem.setIgnore(dto.getIgnore());
          itemList.add(dependencyItem);
        }
      }
      response.setEntrance(entrance);
      response.setInfoItemList(itemList);
    }
    return response;
  }

  public QueryDiffMsgByIdResponseType queryDiffMsgById(String id) {
    QueryDiffMsgByIdResponseType response = new QueryDiffMsgByIdResponseType();
    CompareResultDto compareResultDto = replayCompareResultRepository.queryCompareResultsById(id);
    CompareResultDetail compareResultDetail = CompareResultMapper.INSTANCE.detailFromDto(
        compareResultDto);
    fillCompareResultDetail(compareResultDto, compareResultDetail);
    response.setCompareResultDetail(compareResultDetail);
    return response;
  }

  public QueryLogEntityResponseType queryLogEntity(QueryLogEntityRequestTye request) {
    QueryLogEntityResponseType response = new QueryLogEntityResponseType();
    CompareResultDto dto = replayCompareResultRepository.queryCompareResultsById(
        request.getCompareResultId());
    List<LogEntity> logs = dto.getLogs();
    response.setLogEntity(logs.get(request.getLogIndex()));
    response.setDiffResultCode(dto.getDiffResultCode());
    return response;
  }

  private int computeItemStatus(CompareResultDto compareResult) {
    UnmatchedCategory unmatchedCategory = UnmatchedCategory.computeCategory(compareResult);
    if (unmatchedCategory == UnmatchedCategory.UNKNOWN) {
      return -1;
    }
    return unmatchedCategory.getCode();
  }

  private void fillCompareResultDetail(CompareResultDto compareResultDto,
      CompareResultDetail compareResultDetail) {
    List<LogEntity> logEntities = compareResultDto.getLogs();
    if (CollectionUtils.isEmpty(logEntities)) {
      return;
    }

    if (compareResultDto.getDiffResultCode() == DiffResultCode.COMPARED_INTERNAL_EXCEPTION) {
      LogEntity logEntity = logEntities.get(0);
      CompareResultDetail.LogInfo logInfo = new CompareResultDetail.LogInfo();
      logInfo.setUnmatchedType(logEntity.getPathPair().getUnmatchedType());
      logInfo.setNodePath(Collections.emptyList());
      compareResultDetail.setLogInfos(Collections.singletonList(logInfo));
      compareResultDetail.setExceptionMsg(logEntity.getLogInfo());
    } else {
      HashMap<MutablePair<String, Integer>, CompareResultDetail.LogInfo> logInfoMap = new HashMap<>();
      int size = logEntities.size();
      for (int i = 0; i < size; i++) {
        LogEntity logEntity = logEntities.get(i);
        UnmatchedPairEntity pathPair = logEntity.getPathPair();
        int unmatchedType = pathPair.getUnmatchedType();
        List<NodeEntity> leftUnmatchedPath = pathPair.getLeftUnmatchedPath();
        List<NodeEntity> rightUnmatchedPath = pathPair.getRightUnmatchedPath();
        int leftUnmatchedPathSize = leftUnmatchedPath == null ? 0 : leftUnmatchedPath.size();
        int rightUnmatchedPathSize = rightUnmatchedPath == null ? 0 : rightUnmatchedPath.size();
        List<NodeEntity> nodePath =
            leftUnmatchedPathSize >= rightUnmatchedPathSize ? leftUnmatchedPath
                : rightUnmatchedPath;
        MutablePair<String, Integer> tempPair =
            new MutablePair<>(ListUtils.getFuzzyPathStr(nodePath), unmatchedType);
        CompareResultDetail.LogInfo logInfo;
        if (!logInfoMap.containsKey(tempPair)) {
          logInfo = new CompareResultDetail.LogInfo();
          logInfo.setUnmatchedType(unmatchedType);
          logInfo.setNodePath(nodePath);
          logInfo.setLogIndex(i);
          logInfoMap.put(tempPair, logInfo);
        } else {
          logInfo = logInfoMap.get(tempPair);
        }
        logInfo.setCount(logInfo.getCount() + 1);
      }
      compareResultDetail.setLogInfos(new ArrayList<>(logInfoMap.values()));
    }
  }
}
