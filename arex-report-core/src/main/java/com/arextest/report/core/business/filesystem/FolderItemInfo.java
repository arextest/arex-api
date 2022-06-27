package com.arextest.report.core.business.filesystem;

import com.arextest.report.core.repository.FSFolderRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
}
