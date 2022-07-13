package com.arextest.report.core.repository;


import com.arextest.report.model.dto.manualreport.ManualReportPlanDto;

public interface ManualReportPlanRepository extends RepositoryProvider {
    ManualReportPlanDto initManualReportPlan(ManualReportPlanDto dto);
}
