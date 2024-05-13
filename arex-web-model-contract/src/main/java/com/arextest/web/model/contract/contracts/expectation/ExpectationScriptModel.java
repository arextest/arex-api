package com.arextest.web.model.contract.contracts.expectation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
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
    @NotBlank(message = "operationId can not be blank")
    private String operationId;
    /**
     * Script alias
     */
    private String alias;
    /**
     * Script content
     */
    @NotBlank(message = "content can not be blank")
    public String content;
    /**
     * Script normalized content
     */
    private String normalizedContent;
    /**
     * Script is valid, null for all, 0 true/1 false
     */
    private Boolean valid;
    /**
     * Script expiration time
     */
    private Long expirationTime;
    /**
     * 0 for specified operation
     * 1 for all operation
     */
    private byte scope;
    private String dataChangeCreateBy;
    private String dataChangeUpdateBy;
    private Long dataChangeCreateTime;
    private Long dataChangeUpdateTime;
    /**
     * extract operation list
     */
    private List<ScriptExtractOperationModel> extractOperationList;
    @JsonIgnore
    private transient List<ScriptAssertionModel> invalidExtractAssertList;
}
