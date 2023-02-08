package com.arextest.web.core.business.filesystem;

import com.arextest.web.core.repository.FSTraceLogRepository;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTraceLogDto;
import com.arextest.web.model.enums.TraceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author b_yu
 * @since 2023/1/19
 */
@Slf4j
@Component
public class FSTraceLogUtils {
    @Resource
    private FSTraceLogRepository fsTraceLogRepository;


    public void logUpdateItem(String userName, FSItemDto itemDto) {
        List<FSItemDto> items = new ArrayList<>();
        items.add(itemDto);
        traceLog(TraceType.UPDATE_ITEM,
                userName,
                itemDto.getWorkspaceId(),
                itemDto.getId(),
                itemDto.getParentId(),
                null,
                items);
    }

    public void logDeleteItem(String userName,
            String workspaceId,
            String infoId,
            String parentId,
            List<FSItemDto> items,
            FSNodeDto node) {

        traceLog(TraceType.DELETE_ITEM,
                userName,
                workspaceId,
                infoId,
                parentId,
                node,
                items);
    }

    public void traceLog(int traceType,
            String userName,
            String workspaceId,
            String infoId,
            String parentId,
            FSNodeDto node,
            List<FSItemDto> items) {
        FSTraceLogDto traceLogDto = new FSTraceLogDto();
        traceLogDto.setTraceType(traceType);
        traceLogDto.setUserName(userName);
        traceLogDto.setWorkspaceId(workspaceId);
        traceLogDto.setInfoId(infoId);
        traceLogDto.setParentId(parentId);
        traceLogDto.setNode(node);
        traceLogDto.setItems(items);

        if (!fsTraceLogRepository.saveTraceLog(traceLogDto)) {
            LOGGER.error("Failed to log trace");
        }
    }
}
