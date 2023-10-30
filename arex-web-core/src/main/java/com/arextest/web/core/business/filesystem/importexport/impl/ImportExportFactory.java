package com.arextest.web.core.business.filesystem.importexport.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.arextest.web.core.business.filesystem.importexport.ImportExport;

/**
 * @author b_yu
 * @since 2022/9/30
 */
@Component
public class ImportExportFactory {
    private static final String IMPORT_EXPORT = "ImportExport-";
    @Resource
    private Map<String, ImportExport> importExportMap;

    public ImportExport getImportExport(Integer type) {
        String key = IMPORT_EXPORT + type.toString();
        if (importExportMap.containsKey(key)) {
            return importExportMap.get(key);
        }
        return null;
    }
}
