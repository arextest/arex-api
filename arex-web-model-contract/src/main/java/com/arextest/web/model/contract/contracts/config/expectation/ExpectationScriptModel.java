package com.arextest.web.model.contract.contracts.config.expectation;

import java.util.Map;
import java.util.Set;
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
    public String normalizedContent;
    /**
     * Script is valid, null for all, 0 true/1 false
     */
    public Boolean valid;
    /**
     * Script expiration time
     */
    public long expirationTime;
    /**
     * 0 for specified operation
     * 1 for all operation
     */
    private byte scope;
    private String dataChangeCreateBy;
    private String dataChangeUpdateBy;
    private String normalizedScript;
    /**
     * import operation list eg: [{soaConsumer, xxx.service1, xxx.service2}]
     */
    private Map<String, Set<ScriptImportsOperationModel>> scriptImportOperationMap;
}
