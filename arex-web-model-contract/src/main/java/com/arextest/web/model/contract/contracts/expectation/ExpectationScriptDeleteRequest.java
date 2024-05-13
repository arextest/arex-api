package com.arextest.web.model.contract.contracts.expectation;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExpectationScriptDeleteRequest {
    @NotBlank(message = "id can not be blank")
    private String id;
    /**
     * app id
     */
    @NotBlank(message = "appId can not be blank")
    private String appId;
}
