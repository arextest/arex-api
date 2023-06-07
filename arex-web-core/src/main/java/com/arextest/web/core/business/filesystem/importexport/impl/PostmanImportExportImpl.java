package com.arextest.web.core.business.filesystem.importexport.impl;

import com.arextest.web.core.business.filesystem.importexport.ImportExport;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTreeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postman.collection.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("ImportExport-2")
public class PostmanImportExportImpl implements ImportExport {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean importItem(FSTreeDto fsTreeDto, String[] path, String importString) {
        Collection pmcTest = Collection.pmcFactory("/path/to/your/exported/collection.json");
        return false;
    }

    @Override
    public String exportItem(List<FSNodeDto> nodes, Map<String, FSItemDto> itemInfos) {
        LOGGER.error("Temporarily not supported");
        return null;
    }
}
