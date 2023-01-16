package com.arextest.web.core.business.filesystem;

import com.arextest.web.core.repository.FSCaseRepository;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Component("ItemInfo-2")
public class CaseItemInfo implements ItemInfo {

    @Resource
    private FSCaseRepository fsCaseRepository;

    @Override
    public String initItem(String parentId, Integer parentNodeType, String workspaceId, String name) {
        return fsCaseRepository.initCase(parentId, parentNodeType, workspaceId, name);
    }
    @Override
    public String saveItem(FSItemDto dto) {
        FSCaseDto caseDto = fsCaseRepository.saveCase((FSCaseDto) dto);
        return caseDto.getId();
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
    public String duplicate(String parentId, String infoId, String name) {
        FSCaseDto dto = fsCaseRepository.queryCase(infoId, false);
        dto.setId(null);
        dto.setParentId(parentId);
        dto.setName(name);
        return fsCaseRepository.duplicate(dto);
    }
    @Override
    public List<FSItemDto> queryByIds(List<String> ids) {
        return fsCaseRepository.queryCases(ids, false);
    }
    @Override
    public FSItemDto queryById(String id) {
        return fsCaseRepository.queryCase(id, false);
    }
}
