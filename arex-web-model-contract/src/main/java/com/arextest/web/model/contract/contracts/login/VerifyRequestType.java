package com.arextest.web.model.contract.contracts.login;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VerifyRequestType {
    @NotBlank(message = "UserName cannot be empty")
    private String userName;
    @NotBlank(message = "verification code cannot be empty")
    private String verificationCode;
}
