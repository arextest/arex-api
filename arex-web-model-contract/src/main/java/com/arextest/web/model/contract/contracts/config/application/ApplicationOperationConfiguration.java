package com.arextest.web.model.contract.contracts.config.application;

import com.arextest.web.model.contract.contracts.common.Dependency;
import com.arextest.web.model.contract.contracts.config.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;


@Getter
@Setter
public class ApplicationOperationConfiguration extends AbstractConfiguration implements OperationDescription {
    private String id;
    private String appId;
    private String serviceId;
    private String operationName;
    @Deprecated
    private String operationType;
    private Set<String> operationTypes;
    private String operationResponse;
    private Integer recordedCaseCount;
    private List<Dependency> dependencies;
}
