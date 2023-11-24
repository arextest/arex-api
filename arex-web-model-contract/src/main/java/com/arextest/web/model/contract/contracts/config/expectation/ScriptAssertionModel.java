package com.arextest.web.model.contract.contracts.config.expectation;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @since 2023/11/29
 */
@Data
public class ScriptAssertionModel {

    public String methodName;
    private String expected;
    private String shortServiceName;
    private String fullServiceName;
    private String categoryName;
    private String path;
    private String originalText;

    public ScriptAssertionModel(String originalText) {
        this.originalText = originalText;
    }

    public boolean validate() {
        return methodName != null
            && expected != null
            && fullServiceName != null
            && path != null;
    }

    public String regenerate() {
        String[] arrays = StringUtils.splitPreserveAllTokens(originalText, "(");
        return String.format("%s(\"%s\", \"%s\", \"%s\", %s",
            arrays[0], categoryName, fullServiceName, path, arrays[1]);
    }
}
