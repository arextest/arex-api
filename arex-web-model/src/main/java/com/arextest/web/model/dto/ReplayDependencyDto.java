package com.arextest.web.model.dto;

import com.arextest.web.model.contract.contracts.common.Dependency;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@Data
@EqualsAndHashCode(callSuper = true)
public class ReplayDependencyDto extends BaseDto {
    private String operationId;
    private String operationName;
    private String operationType;
    private String planItemId;
    private String recordId;
    private List<Dependency> dependencies;
}
