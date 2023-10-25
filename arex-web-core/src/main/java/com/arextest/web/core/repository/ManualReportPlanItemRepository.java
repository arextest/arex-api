package com.arextest.web.core.repository;

import com.arextest.web.model.dto.manualreport.ManualReportPlanItemDto;

public interface ManualReportPlanItemRepository extends RepositoryProvider {
    ManualReportPlanItemDto initManualReportPlanItems(ManualReportPlanItemDto planItemDto);
}
