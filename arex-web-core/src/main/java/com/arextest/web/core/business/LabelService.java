package com.arextest.web.core.business;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.arextest.web.core.repository.LabelRepository;
import com.arextest.web.model.contract.contracts.label.LabelType;
import com.arextest.web.model.contract.contracts.label.RemoveLabelRequestType;
import com.arextest.web.model.contract.contracts.label.SaveLabelRequestType;
import com.arextest.web.model.dto.LabelDto;
import com.arextest.web.model.mapper.LabelMapper;

/**
 * @author b_yu
 * @since 2022/11/21
 */
@Component
public class LabelService {

    @Resource
    private LabelRepository labelRepository;

    public boolean saveLabel(SaveLabelRequestType request) {
        LabelDto labelDto = LabelMapper.INSTANCE.dtoFromContract(request);
        return labelRepository.saveLabel(labelDto);
    }

    public boolean removeLabel(RemoveLabelRequestType request) {
        return labelRepository.removeLabel(request.getId());
    }

    public List<LabelType> queryLabelsByWorkspaceId(String workspaceId) {
        List<LabelDto> dtos = labelRepository.queryLabelsByWorkspaceId(workspaceId);
        if (CollectionUtils.isEmpty(dtos)) {
            return Collections.emptyList();
        }
        return dtos.stream().map(LabelMapper.INSTANCE::contractFromDto).collect(Collectors.toList());
    }
}
