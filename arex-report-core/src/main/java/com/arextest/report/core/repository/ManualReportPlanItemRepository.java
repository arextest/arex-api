package com.arextest.report.core.repository;


import com.arextest.report.model.dto.manualreport.ManualReportPlanItemDto;

import java.util.List;

public interface ManualReportPlanItemRepository extends RepositoryProvider {
    ManualReportPlanItemDto initManualReportPlanItems(ManualReportPlanItemDto planItemDto);
}
