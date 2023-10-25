package com.arextest.web.core.business.iosummary;

import cn.hutool.core.collection.CollectionUtil;
import com.arextest.web.core.repository.CaseSummaryRepository;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.iosummary.CaseSummary;
import com.arextest.web.model.dto.iosummary.UnmatchedCategory;
import com.arextest.web.model.enums.DiffResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SummaryService {

    @Autowired
    CaseSummaryRepository caseSummaryRepository;

    @Autowired
    SceneReportService sceneReportService;

    /**
     * analyze the compared results
     */
    public void analysis(List<CompareResultDto> compareResults) {
        if (CollectionUtil.isEmpty(compareResults)) {
            return;
        }

        CompareResultDto compareResultDto = compareResults.get(0);
        CaseSummary summary = compareResults.stream()
                .filter(r -> r.getDiffResultCode() != DiffResultCode.COMPARED_WITHOUT_DIFFERENCE)
                .reduce(CaseSummary.builder(), this::analysis0, (b1, b2) -> null)
                .planId(compareResultDto.getPlanId())
                .planItemId(compareResultDto.getPlanItemId())
                .recordId(compareResultDto.getRecordId())
                .rePlayId(compareResultDto.getReplayId())
                .build();
        summary.setCategoryKey(summary.categoryKey());
        summary.setGroupKey(summary.groupKey());
        caseSummaryRepository.upsert(summary);
        sceneReportService.report(summary);
    }

    private CaseSummary.Builder analysis0(CaseSummary.Builder builder, CompareResultDto compareResultDto) {
        UnmatchedCategory category = UnmatchedCategory.computeCategory(compareResultDto);
        if (category == UnmatchedCategory.UNKNOWN) {
            return builder.failed();
        } else if (category == UnmatchedCategory.MATCHED) {
            return builder.success();
        }

        return builder.detail(compareResultDto.getCategoryName(), compareResultDto.getOperationName(), category);
    }
}
