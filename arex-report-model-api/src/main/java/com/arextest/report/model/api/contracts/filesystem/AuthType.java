package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class AuthType {
    private String authType;
    private Boolean authActive;
    private String token;
}
