package com.arextest.report.core.repository;

/**
 * @author b_yu
 * @since 2022/6/15
 */
public interface FSCaseRepository {
    String initCase();
    Boolean removeCases(String id);
}
