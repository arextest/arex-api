package com.arextest.report.core.repository;

import com.arextest.report.model.dto.filesystem.FSCaseDto;

/**
 * @author b_yu
 * @since 2022/6/15
 */
public interface FSCaseRepository extends RepositoryProvider {
    String initCase();

    Boolean removeCases(String id);

    FSCaseDto saveCase(FSCaseDto dto);

    FSCaseDto queryCase(String id);
}
