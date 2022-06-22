package com.arextest.report.core.repository;

import com.arextest.report.model.dto.filesystem.FSCaseDto;


public interface FSCaseRepository extends RepositoryProvider {
    String initCase();

    Boolean removeCases(String id);

    FSCaseDto saveCase(FSCaseDto dto);

    FSCaseDto queryCase(String id);
}
