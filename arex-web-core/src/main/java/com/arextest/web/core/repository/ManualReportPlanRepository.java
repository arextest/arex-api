package com.arextest.web.core.repository;


import com.arextest.web.model.dto.manualreport.ManualReportPlanDto;

public interface ManualReportPlanRepository extends RepositoryProvider {
    ManualReportPlanDto initManualReportPlan(ManualReportPlanDto dto);
}
