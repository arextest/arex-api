package com.arextest.report.core.repository;

import com.arextest.report.model.dto.PlanItemDto;

import java.util.List;


public interface ReportPlanItemStatisticRepository extends RepositoryProvider {
    PlanItemDto updatePlanItems(PlanItemDto planItem);

    
    boolean findAndModifyBaseInfo(PlanItemDto result);

    
    List<PlanItemDto> findByPlanIdAndPlanItemId(String planId, String planItemId);

    
    List<PlanItemDto> findByPlanIds(List<String> planIds);

    
    List<Integer> findStatusesByPlanId(String planId);

    
    PlanItemDto changePlanItemStatus(String planItemId, Integer status, Integer totalCaseCount);

}
