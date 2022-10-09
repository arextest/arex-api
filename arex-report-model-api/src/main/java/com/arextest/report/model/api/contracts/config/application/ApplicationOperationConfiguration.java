package com.arextest.report.model.api.contracts.config.application;


import com.arextest.report.model.api.contracts.config.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApplicationOperationConfiguration extends AbstractConfiguration implements OperationDescription {
    private String id;
    private String appId;
    private String serviceId;
    private String operationName;
    private int operationType;
    private String operationResponse;
    private Integer recordedCaseCount;
}
