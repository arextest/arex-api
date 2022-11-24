package com.arextest.web.model.contract.contracts.login;

import lombok.Data;

@Data
public class UpdateUserProfileRequestType {
    private String userName;
    private String profile;
}
