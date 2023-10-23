package com.arextest.web.model.dao.mongodb.expectation;

import com.arextest.web.model.dao.mongodb.ModelBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Document(collection = "ConfigExpectationScript")
public class ExpectationScriptEntity extends ModelBase {
    /**
     * app id
     */
    private String appId;
    /**
     * Script title, eg: classA.methodB
     */
    private String title;
    /**
     * Script content
     */
    public String content;
    /**
     * Script is valid
     */
    public Boolean valid;
    /**
     * Script expiration time
     */
    public long expirationTime;
    /**
     /**
     * 0 specified: for specified operation
     * 1 global: for all operation
     */
    private byte scope;
    private String dataChangeCreateBy;
    private String dataChangeUpdateBy;
}
