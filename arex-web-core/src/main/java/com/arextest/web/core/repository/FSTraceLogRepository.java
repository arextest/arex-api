package com.arextest.web.core.repository;

import java.util.List;

import com.arextest.web.model.dto.filesystem.FSTraceLogDto;

/**
 * @author b_yu
 * @since 2023/1/18
 */
public interface FSTraceLogRepository {
    boolean saveTraceLog(FSTraceLogDto traceLogDto);

    FSTraceLogDto queryTraceLog(String id);

    List<FSTraceLogDto> queryTraceLogsByWorkspaceId(String workspaceId);
}
