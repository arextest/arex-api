package com.arextest.report.model.api.contracts.login;

import lombok.Data;

@Data
public class UpdateUserProfileRequestType {
    private String email;
    private String profile;
}
