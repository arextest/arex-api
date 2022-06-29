package com.arextest.report.core.business.filesystem;

import com.arextest.report.core.repository.FSCaseRepository;
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
}
