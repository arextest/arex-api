package com.arextest.web.model.contract.contracts.config.expectation;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @since 2023/11/29
 */
@Data
public class ScriptExtractAssertModel {

    public String methodName;
    private String expected;
    private String shortServiceName;
    private String operationName;
    private String categoryName;
    private String path;
    private String originalText;

    public ScriptExtractAssertModel(String originalText) {
        this.originalText = originalText;
    }

    public boolean validate() {
        return methodName != null
            && expected != null
            && operationName != null
            && path != null;
    }

    public String rebuild() {
        String[] arrays = StringUtils.splitPreserveAllTokens(originalText, "(");
        if (arrays == null || arrays.length != 2) {
            return originalText;
        }
        return String.format("%s(\"%s\", \"%s\", \"%s\", '%s', %s",
            arrays[0], categoryName, operationName, path, originalText, arrays[1]);
    }
}
