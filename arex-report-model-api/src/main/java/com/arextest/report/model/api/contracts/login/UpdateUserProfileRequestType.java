package com.arextest.report.model.api.contracts.login;

import lombok.Data;

@Data
public class UpdateUserProfileRequestType {
    private String userName;
    private String profile;
}
