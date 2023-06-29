package com.arextest.web.model.contract.contracts.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dependency {
    private String operationId;
    private String operationName;
}
