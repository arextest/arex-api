package com.arextest.report.core.business;

import com.arextest.report.core.repository.ReplayCompareResultRepository;
import com.arextest.report.model.api.contracts.*;
import com.arextest.report.model.api.contracts.common.CompareResult;
import com.arextest.report.model.dto.CompareResultDto;
import com.arextest.report.model.enums.DiffResultCode;
import com.arextest.report.model.mapper.CompareResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Component
public class QueryReplayMsgService {

    @Resource
    private ReplayCompareResultRepository repository;

    private static final int BIG_MESSAGE_THRESHOLD = 5 * 1024 * 1024;

    public QueryReplayMsgResponseType queryReplayMsg(QueryReplayMsgRequestType request) {
        QueryReplayMsgResponseType response = new QueryReplayMsgResponseType();
        CompareResultDto dto = repository.queryCompareResultsByObjectId(request.getId());
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
        CompareResultDto dto = repository.queryCompareResultsByObjectId(request.getId());
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
            LOGGER.error("downloadReplayMsg", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LOGGER.error("downloadReplayMsg", e);
            }
        }
    }

    public QueryFullLinkMsgResponseType queryFullLinkMsg(QueryFullLinkMsgRequestType request) {
        QueryFullLinkMsgResponseType response = new QueryFullLinkMsgResponseType();
        List<CompareResultDto> dtos = repository.queryCompareResultsByRecordId(request.getRecordId());
        if (dtos == null) {
            return response;
        }
        List<CompareResult> compareResults = dtos.stream()
                .map(CompareResultMapper.INSTANCE::contractFromDtoLogsLimitDisplay)
                .collect(Collectors.toList());
        response.setCompareResults(compareResults);
        return response;
    }

}
