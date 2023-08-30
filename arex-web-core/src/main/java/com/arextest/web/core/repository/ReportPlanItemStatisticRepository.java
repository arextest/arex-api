package com.arextest.web.core.repository;

import com.arextest.web.model.dto.PlanItemDto;

import java.util.List;


public interface ReportPlanItemStatisticRepository extends RepositoryProvider {
    PlanItemDto updatePlanItems(PlanItemDto planItem);


    boolean findAndModifyBaseInfo(PlanItemDto result);


    List<PlanItemDto> findByPlanIdAndPlanItemId(String planId, String planItemId);

    PlanItemDto findByPlanItemId(String planItemId);

    List<PlanItemDto> findByPlanIds(List<String> planIds);


    List<Integer> findStatusesByPlanId(String planId);


    PlanItemDto changePlanItemStatus(String planItemId, Integer status, Integer totalCaseCount, String errorMessage);

    boolean deletePlanItemsByPlanId(String planId);

    boolean findAndModifyCaseMap(PlanItemDto result);
}
