package com.arextest.web.core.business.filesystem.importexport.impl;

import com.arextest.web.core.business.filesystem.importexport.ImportExport;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component("ImportExport-2")
public class PostmanImportExportImpl implements ImportExport {
    @Override
    public boolean importItem(FSTreeDto fsTreeDto, String[] path, String importString) {
        return false;
    }

    @Override
    public String exportItem(List<FSNodeDto> nodes, Map<String, FSItemDto> itemInfos) {
        return null;
    }
}
