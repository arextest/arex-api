package com.arextest.web.core.business.filesystem.importexport;

import java.util.List;
import java.util.Map;

import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTreeDto;

/**
 * @author b_yu
 * @since 2022/9/30
 */
public interface ImportExport {
    boolean importItem(FSTreeDto fsTreeDto, String[] path, String importString);

    String exportItem(List<FSNodeDto> nodes, Map<String, FSItemDto> itemInfos);
}
