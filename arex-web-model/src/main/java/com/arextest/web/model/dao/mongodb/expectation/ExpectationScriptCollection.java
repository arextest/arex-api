package com.arextest.web.model.dao.mongodb.expectation;

import com.arextest.web.model.dao.mongodb.ModelBase;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Document(collection = "ConfigExpectationScript")
public class ExpectationScriptCollection extends ModelBase {
    /**
     * app id
     */
    private String appId;
    /**
     * operation id
     */
    private String operationId;
    /**
     * Script alias
     */
    private String alias;
    /**
     * Script content
     */
    public String content;
    /**
     * Script normalized content
     */
    private String normalizedContent;
    /**
     * Script is valid
     */
    public Boolean valid;
    /**
     * Script expiration time
     */
    public Long expirationTime;
    /**
     /**
     * 0 specified: for specified operation
     * 1 global: for all operation
     */
    private byte scope;
    private String dataChangeCreateBy;
    private String dataChangeUpdateBy;
    /**
     * extract operation list
     */
    private List<ScriptExtractOperationCollection> extractOperationList;

    @Data
    public static class ScriptExtractOperationCollection {
        private String categoryName;
        private String variableName;
        private String operationName;
        private String originalText;
    }
}
