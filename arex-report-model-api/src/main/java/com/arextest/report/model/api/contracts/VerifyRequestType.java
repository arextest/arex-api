package com.arextest.report.model.api.contracts;

import lombok.Data;

@Data
public class VerifyRequestType {
    private String email;
    private String verificationCode;
}
