package com.arextest.web.core.repository;

import com.arextest.web.model.dto.filesystem.FSTraceLogDto;
import java.util.List;

/**
 * @author b_yu
 * @since 2023/1/18
 */
public interface FSTraceLogRepository {

  boolean saveTraceLog(FSTraceLogDto traceLogDto);

  FSTraceLogDto queryTraceLog(String id);

  List<FSTraceLogDto> queryTraceLogsByWorkspaceId(String workspaceId);
}
