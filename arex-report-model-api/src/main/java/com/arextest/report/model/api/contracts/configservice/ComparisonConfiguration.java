package com.arextest.report.model.api.contracts.configservice;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ComparisonConfiguration {
    
    private String appId;

    
    private Long operationId;

    private int categoryType;
    private List<ComparisonDetailsConfiguration> detailsList;
}
