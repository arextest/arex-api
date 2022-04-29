package com.arextest.report.core.repository;

import com.arextest.report.model.dto.PlanItemDto;

import java.util.List;


public interface ReportPlanItemStatisticRepository extends RepositoryProvider {
    PlanItemDto updatePlanItems(PlanItemDto planItem);

    
    boolean findAndModifyBaseInfo(PlanItemDto result);

    
    List<PlanItemDto> findByPlanIdAndPlanItemId(Long planId, Long planItemId);

    
    List<PlanItemDto> findByPlanIds(List<Long> planIds);

    
    List<Integer> findStatusesByPlanId(Long planId);

    
    PlanItemDto changePlanItemStatus(Long planItemId, Integer status, Integer totalCaseCount);

}
