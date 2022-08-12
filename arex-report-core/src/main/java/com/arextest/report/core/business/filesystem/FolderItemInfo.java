package com.arextest.report.core.business.filesystem;

import com.arextest.report.core.repository.FSFolderRepository;
import com.arextest.report.model.dao.mongodb.FSFolderCollection;
import com.arextest.report.model.dto.filesystem.FSFolderDto;
import com.arextest.report.model.enums.FSInfoItem;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Component("3")
public class FolderItemInfo implements ItemInfo {

    @Resource
    private FSFolderRepository fsFolderRepository;

    @Override
    public String saveItem(String parentId, Integer parentNodeType) {
        return fsFolderRepository.initFolder(parentId, parentNodeType);
    }
    @Override
    public Boolean removeItem(String infoId) {
        return fsFolderRepository.removeFolder(infoId);
    }
    @Override
    public Boolean removeItems(Set<String> infoIds) {
        return fsFolderRepository.removeFolders(infoIds);
    }

    @Override
    public String duplicate(String parentId, String infoId) {
        FSFolderDto dto = fsFolderRepository.queryById(infoId);
        dto.setId(null);
        dto.setParentId(parentId);
        return fsFolderRepository.duplicate(dto);
    }
}
