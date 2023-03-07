package com.arextest.web.core.business.iosummary;

import cn.hutool.core.collection.CollectionUtil;
import com.arextest.web.core.repository.CaseSummaryRepository;
import com.arextest.web.model.contract.contracts.common.LogEntity;
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

    /**
     * 分析比对结果
     * todo: 根因分析后续看情况是否需要支持
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
        caseSummaryRepository.save(summary);
    }

    private CaseSummary.Builder analysis0(CaseSummary.Builder builder, CompareResultDto compareResultDto) {
        UnmatchedCategory category = computeCategory(compareResultDto);
        if (category == UnmatchedCategory.UNKNOWN) {
            return builder.failed();
        } else if (category == UnmatchedCategory.MATCHED) {
            return builder.success();
        }

        return builder.detail(compareResultDto.getCategoryName(), compareResultDto.getOperationName(), category);
    }

    private UnmatchedCategory computeCategory(CompareResultDto compareResult) {
        switch (compareResult.getDiffResultCode()) {
            case DiffResultCode.COMPARED_INTERNAL_EXCEPTION:
            case DiffResultCode.SEND_FAILED_NOT_COMPARE:
                return UnmatchedCategory.UNKNOWN;
            default: {
                List<LogEntity> entities = compareResult.getLogs();
                if (entities == null || entities.size() == 0) {
                    return UnmatchedCategory.UNKNOWN;
                } else if (entities.size() > 1) {
                    return UnmatchedCategory.VALUE_DIFF;
                }

                // LogEntity entity = entities.get(0);
                // if (CollectionUtils.isEmpty(entity.getPathPair().getLeftUnmatchedPath())
                //         && CollectionUtils.isEmpty(entity.getPathPair().getRightUnmatchedPath())) {
                //     if()
                // }
                // if (entity.getBaseValue() == null) {
                //     return UnmatchedCategory.LEFT_MISSING;
                // } else if (entity.getTestValue() == null) {
                //     return UnmatchedCategory.RIGHT_MISSING;
                // }
                if (compareResult.getBaseMsg() == null) {
                    return UnmatchedCategory.LEFT_MISSING;
                } else if (compareResult.getTestMsg() == null) {
                    return UnmatchedCategory.RIGHT_MISSING;
                }
                return UnmatchedCategory.VALUE_DIFF;
            }
        }
    }
}
