package com.arextest.web.model.contract.contracts.config.expectation;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExpectationScriptQueryRequest {
    @NotBlank(message = "appId can not be blank")
    private String appId;
    private String title;
    private Boolean valid;
    private Long expirationTime;
    private Byte scope;
}
