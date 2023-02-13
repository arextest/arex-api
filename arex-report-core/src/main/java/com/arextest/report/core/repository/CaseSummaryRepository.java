package com.arextest.report.core.repository;

import com.arextest.report.core.business.iosummary.CaseSummary;

import java.util.List;

public interface CaseSummaryRepository extends RepositoryProvider {

    boolean save(CaseSummary summary);

    List<CaseSummary> query(String planItemId);
}
