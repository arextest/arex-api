package com.arextest.web.core.business.filesystem;

import com.arextest.web.core.business.filesystem.pincase.StorageCase;
import com.arextest.web.core.repository.FSCaseRepository;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component("ItemInfo-2")
@Slf4j
@RequiredArgsConstructor
public class CaseItemInfo implements ItemInfo {
  public static final String RECORD_ID_KEY = "arex-record-id";
  private final FSCaseRepository fsCaseRepository;
  private final StorageCase storageCase;

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
    return fsCaseRepository.removeCases(infoIds);
  }

  @Override
  public String duplicate(String parentId, String infoId, String name) {
    FSCaseDto dto = fsCaseRepository.queryCase(infoId, false);
    dto.setId(null);
    dto.setParentId(parentId);
    dto.setName(name);

    if (StringUtils.isNotEmpty(dto.getRecordId())) {
      String newRecordId = storageCase.copyPinnedCase(dto.getRecordId());
      if (StringUtils.isNotEmpty(newRecordId)) {
        dto.setRecordId(newRecordId);
        dto.getHeaders().stream()
            .filter(header -> RECORD_ID_KEY.equals(header.getKey()))
            .findFirst()
            .ifPresent(header -> header.setValue(newRecordId));
      } else {
        LOGGER.error("Duplicate case failed to copy pinned cases, recordId: {}", dto.getRecordId());
      }
    }

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
