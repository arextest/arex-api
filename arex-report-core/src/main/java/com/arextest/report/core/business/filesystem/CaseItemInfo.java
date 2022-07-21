package com.arextest.report.core.business.filesystem;

import com.arextest.report.core.repository.FSCaseRepository;
import com.arextest.report.model.dto.filesystem.FSCaseDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("2")
public class CaseItemInfo implements ItemInfo {

    @Resource
    private FSCaseRepository fsCaseRepository;

    @Override
    public String saveItem(String parentId, Integer parentNodeType) {
        return fsCaseRepository.initCase(parentId, parentNodeType);
    }
    @Override
    public Boolean removeItem(String infoId) {
        return fsCaseRepository.removeCases(infoId);
    }
    @Override
    public String duplicate(String parentId, String infoId) {
        FSCaseDto dto = fsCaseRepository.queryCase(infoId);
        dto.setId(null);
        dto.setParentId(parentId);
        return fsCaseRepository.duplicate(dto);
    }
}
