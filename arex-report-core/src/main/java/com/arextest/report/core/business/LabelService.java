package com.arextest.report.core.business;

import com.arextest.report.core.repository.LabelRepository;
import com.arextest.report.model.api.contracts.label.LabelType;
import com.arextest.report.model.api.contracts.label.RemoveLabelRequestType;
import com.arextest.report.model.api.contracts.label.SaveLabelRequestType;
import com.arextest.report.model.dto.LabelDto;
import com.arextest.report.model.mapper.LabelMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
