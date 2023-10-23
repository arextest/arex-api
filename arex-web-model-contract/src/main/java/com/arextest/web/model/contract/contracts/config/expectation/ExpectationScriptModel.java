package com.arextest.web.model.contract.contracts.config.expectation;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Expectation script config
 */
@Data
public class ExpectationScriptModel {
    private String id;
    /**
     * app id
     */
    @NotBlank(message = "appId can not be blank")
    private String appId;
    /**
     * Script title, eg: classA.methodB
     */
    @NotBlank(message = "title can not be blank")
    private String title;
    /**
     * Script content
     */
    @NotBlank(message = "content can not be blank")
    public String content;
    /**
     * Script is valid, null for all, 0 true/1 false
     */
    public Boolean valid;
    /**
     * Script expiration time
     */
    public long expirationTime;
    /**
     * 0 specified: for specified operation
     * 1 global: for all operation
     */
    private byte scope;
    private String dataChangeCreateBy;
    private String dataChangeUpdateBy;
}
