package com.arextest.web.model.contract.contracts.login;

import lombok.Data;

@Data
public class VerifyRequestType {
    private String userName;
    private String verificationCode;
}
