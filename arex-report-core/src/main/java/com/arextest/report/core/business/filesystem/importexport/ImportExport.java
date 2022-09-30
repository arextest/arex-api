package com.arextest.report.core.business.filesystem.importexport;

import com.arextest.report.model.dto.filesystem.FSItemDto;
import com.arextest.report.model.dto.filesystem.FSNodeDto;

import java.util.List;
import java.util.Map;

/**
 * @author b_yu
 * @since 2022/9/30
 */
public interface ImportExport {
    void Import();
    String export(List<FSNodeDto> nodes, Map<String, FSItemDto> itemInfos);
}
