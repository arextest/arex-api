package com.arextest.web.model.contract.contracts.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserProfileRequestType {

  @NotBlank(message = "UserName cannot be empty")
  private String userName;
  private String profile;
}
