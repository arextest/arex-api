package com.arextest.report.model.api.contracts.configservice;

import lombok.Data;

import java.util.Collection;


@Data
public class CompareConfig {

    private Collection<String> exclusions;
    private Collection<String> inclusions;
    private Collection<ComparisonDetails> sortKeys;
    private Collection<ComparisonDetails> references;

}
