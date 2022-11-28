package com.arextest.web.model.contract.contracts.login;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateUserProfileRequestType {
    @NotBlank(message = "UserName cannot be empty")
    private String userName;
    private String profile;
}
