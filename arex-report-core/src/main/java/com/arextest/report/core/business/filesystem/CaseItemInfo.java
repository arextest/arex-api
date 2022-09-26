package com.arextest.report.core.business.filesystem;

import com.arextest.report.core.repository.FSCaseRepository;
import com.arextest.report.model.dto.filesystem.FSCaseDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Component("2")
public class CaseItemInfo implements ItemInfo {

    @Resource
    private FSCaseRepository fsCaseRepository;

    @Override
    public String saveItem(String parentId, Integer parentNodeType, String workspaceId) {
        return fsCaseRepository.initCase(parentId, parentNodeType, workspaceId);
    }
    @Override
    public Boolean removeItem(String infoId) {
        return fsCaseRepository.removeCase(infoId);
    }
    @Override
    public Boolean removeItems(Set<String> infoIds) {
        return null;
    }
    @Override
    public String duplicate(String parentId, String infoId) {
        FSCaseDto dto = fsCaseRepository.queryCase(infoId);
        dto.setId(null);
        dto.setParentId(parentId);
        return fsCaseRepository.duplicate(dto);
    }
}
