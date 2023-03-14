package com.arextest.web.core.business;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.core.repository.mongo.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.CompareResultDetail;
import com.arextest.web.model.contract.contracts.DownloadReplayMsgRequestType;
import com.arextest.web.model.contract.contracts.FullLinkInfoItem;
import com.arextest.web.model.contract.contracts.FullLinkSummaryDetail;
import com.arextest.web.model.contract.contracts.QueryDiffMsgByIdResponseType;
import com.arextest.web.model.contract.contracts.QueryDiffMsgWithCategoryResponseType;
import com.arextest.web.model.contract.contracts.QueryFullLinkInfoResponseType;
import com.arextest.web.model.contract.contracts.QueryFullLinkMsgRequestType;
import com.arextest.web.model.contract.contracts.QueryFullLinkMsgResponseType;
import com.arextest.web.model.contract.contracts.QueryFullLinkSummaryResponseType;
import com.arextest.web.model.contract.contracts.QueryReplayMsgRequestType;
import com.arextest.web.model.contract.contracts.QueryReplayMsgResponseType;
import com.arextest.web.model.contract.contracts.common.CompareResult;
import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.enums.DiffResultCode;
import com.arextest.web.model.mapper.CompareResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Component
public class QueryReplayMsgService {

    @Resource
    private ReplayCompareResultRepository replayCompareResultRepository;
    @Resource
    private ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

    private static final int BIG_MESSAGE_THRESHOLD = 5 * 1024 * 1024;

    public QueryReplayMsgResponseType queryReplayMsg(QueryReplayMsgRequestType request) {
        QueryReplayMsgResponseType response = new QueryReplayMsgResponseType();
        CompareResultDto dto = replayCompareResultRepository.queryCompareResultsByObjectId(request.getId());
        if (dto == null) {
            return response;
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

    public void downloadReplayMsg(DownloadReplayMsgRequestType request, HttpServletResponse response) {
        CompareResultDto dto = replayCompareResultRepository.queryCompareResultsByObjectId(request.getId());
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
                replayCompareResultRepository.queryCompareResultsByRecordId(request.getPlanItemId(), request.getRecordId());
        if (dtos == null) {
            return response;
        }
        List<CompareResult> compareResults = dtos.stream()
                .map(CompareResultMapper.INSTANCE::contractFromDtoLogsLimitDisplay)
                .collect(Collectors.toList());
        response.setCompareResults(compareResults);
        return response;
    }

    public QueryFullLinkInfoResponseType queryFullLinkInfo(String recordId, String replayId) {
        QueryFullLinkInfoResponseType response = new QueryFullLinkInfoResponseType();
        List<CompareResultDto> dtos =
                replayCompareResultRepository.queryCompareResultsByRecordIdAndReplayId(recordId, replayId);

        if (CollectionUtils.isNotEmpty(dtos)) {
            // judge entrance type by operationId
            String entranceCategoryName = "";
            CompareResultDto compareResultDto = dtos.get(0);
            String operationId = compareResultDto.getOperationId();
            ApplicationOperationConfiguration applicationOperationConfiguration =
                    applicationOperationConfigurationRepository.listByOperationId(operationId);
            if (applicationOperationConfiguration != null) {
                entranceCategoryName = applicationOperationConfiguration.getOperationType();
            }

            FullLinkInfoItem entrance = new FullLinkInfoItem();
            List<FullLinkInfoItem> itemList = new ArrayList<>();
            for (CompareResultDto dto : dtos) {
                if (Objects.equals(dto.getCategoryName(), entranceCategoryName)) {
                    entrance.setId(dto.getId());
                    entrance.setCategoryName(dto.getCategoryName());
                    entrance.setOperationName(dto.getOperationName());
                    entrance.setCode(computeItemStatus(dto));
                } else {
                    FullLinkInfoItem fullLinkInfoItem = new FullLinkInfoItem();
                    fullLinkInfoItem.setId(dto.getId());
                    fullLinkInfoItem.setCategoryName(dto.getCategoryName());
                    fullLinkInfoItem.setOperationName(dto.getOperationName());
                    fullLinkInfoItem.setCode(computeItemStatus(dto));
                    itemList.add(fullLinkInfoItem);
                }
            }
            response.setEntrance(entrance);
            response.setInfoItemList(itemList);
        }
        return response;
    }

    public QueryDiffMsgByIdResponseType queryDiffMsgById(String id) {
        QueryDiffMsgByIdResponseType response = new QueryDiffMsgByIdResponseType();
        CompareResultDto compareResultDto = replayCompareResultRepository.queryCompareResultsByObjectId(id);
        CompareResultDetail compareResultDetail = CompareResultMapper.INSTANCE.detailFromDto(compareResultDto);
        response.setCompareResultDetail(compareResultDetail);
        return response;
    }

    public QueryFullLinkSummaryResponseType queryFullLinkSummary(String recordId, String replayId) {
        QueryFullLinkSummaryResponseType response = new QueryFullLinkSummaryResponseType();
        List<FullLinkSummaryDetail> fullLinkSummaryDetails =
                replayCompareResultRepository.queryFullLinkSummary(recordId, replayId);
        response.setDetails(fullLinkSummaryDetails);
        return response;
    }

    public QueryDiffMsgWithCategoryResponseType queryFullLinkMsgWithCategory(String recordId, String replayId, String categoryName) {
        QueryDiffMsgWithCategoryResponseType response = new QueryDiffMsgWithCategoryResponseType();
        List<CompareResultDetail> compareResultDetails =
                replayCompareResultRepository.queryFullLinkMsgWithCategory(recordId, replayId, categoryName);
        response.setDetailList(compareResultDetails);
        return response;
    }

    private int computeItemStatus(CompareResultDto compareResult) {
        switch (compareResult.getDiffResultCode()) {
            case DiffResultCode.COMPARED_INTERNAL_EXCEPTION:
            case DiffResultCode.SEND_FAILED_NOT_COMPARE:
                return FullLinkInfoItem.ItemStatus.EXCEPTION;
            case DiffResultCode.COMPARED_WITHOUT_DIFFERENCE:
                return FullLinkInfoItem.ItemStatus.SUCCESS;
            default: {
                List<LogEntity> entities = compareResult.getLogs();
                if (entities == null || entities.size() == 0) {
                    return FullLinkInfoItem.ItemStatus.EXCEPTION;
                } else if (entities.size() > 1) {
                    return FullLinkInfoItem.ItemStatus.VALUE_DIFF;
                }

                if (compareResult.getBaseMsg() == null) {
                    return FullLinkInfoItem.ItemStatus.LEFT_CALL_MISSING;
                } else if (compareResult.getTestMsg() == null) {
                    return FullLinkInfoItem.ItemStatus.RIGHT_CALL_MISSING;
                }
                return FullLinkInfoItem.ItemStatus.VALUE_DIFF;
            }
        }
    }

}
