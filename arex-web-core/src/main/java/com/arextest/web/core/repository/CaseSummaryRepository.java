package com.arextest.web.core.repository;

import java.util.List;

import com.arextest.web.model.dto.iosummary.CaseSummary;

public interface CaseSummaryRepository extends RepositoryProvider {

    boolean save(CaseSummary summary);

    boolean upsert(CaseSummary summary);

    List<CaseSummary> query(String planId, String planItemId);
}
