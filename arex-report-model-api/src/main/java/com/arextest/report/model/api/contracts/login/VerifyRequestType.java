package com.arextest.report.model.api.contracts.login;

import lombok.Data;

@Data
public class VerifyRequestType {
    private String userName;
    private String verificationCode;
}
