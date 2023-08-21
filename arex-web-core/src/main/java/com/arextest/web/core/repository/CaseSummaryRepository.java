package com.arextest.web.core.repository;


import com.arextest.web.model.dto.iosummary.CaseSummary;

import java.util.List;

public interface CaseSummaryRepository extends RepositoryProvider {

    boolean save(CaseSummary summary);

    boolean upsert(CaseSummary summary);

    List<CaseSummary> query(String planId, String planItemId);
}
