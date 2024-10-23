package com.arextest.web.model.contract.contracts.environment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveEnvironmentRequestType {

  @NotNull(message = "Env cannot be empty")
  private EnvironmentType env;
}
