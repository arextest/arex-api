package com.arextest.report.model.api.contracts.login;

import lombok.Data;

@Data
public class LoginAsGuestResponseType {
    private String userName;
    private boolean success;
    private String accessToken;
    private String refreshToken;
}
