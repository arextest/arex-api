package com.arextest.web.core.business.filesystem.recovery;

import com.arextest.web.model.dto.filesystem.FSTraceLogDto;

/**
 * @author b_yu
 * @since 2023/2/7
 */
public interface RecoveryService {
    boolean recovery(FSTraceLogDto log);
}
