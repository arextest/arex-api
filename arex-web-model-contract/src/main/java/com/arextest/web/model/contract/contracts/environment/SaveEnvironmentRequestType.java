package com.arextest.web.model.contract.contracts.environment;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveEnvironmentRequestType {
    @NotNull(message = "Env cannot be empty")
    private EnvironmentType env;
}
