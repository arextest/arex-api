package com.arextest.web.model.contract.contracts.expectation;

import lombok.Data;

/**
 * @since 2023/11/29
 */
@Data
public class ScriptAssertionModel {

    private String category;
    public String method;
    private String operation;
    private String expected;
    private String actual;
    private String shortServiceName;
    private String path;
    private String testName;
    private String originalText;

    public ScriptAssertionModel(String originalText) {
        this.originalText = originalText;
    }

    public boolean validate() {
        return category != null && method != null && operation != null;
    }

    public String rebuild() {
        StringBuilder builder = new StringBuilder();
        builder.append("arex.execute({\n");
        builder.append("  category: \"").append(category).append("\",\n");
        builder.append("  method: \"").append(method).append("\",\n");
        builder.append("  operation: \"").append(operation).append("\",\n");
        builder.append("  path: \"").append(path).append("\",\n");
        builder.append("  actual: ").append(actual).append(",\n");
        if (expected != null) {
            builder.append("  expected: ").append(expected).append(",\n");
        }
        builder.append("  testName: \"").append(testName).append("\",\n");
        builder.append("  originalText: \"").append(originalText).append("\"\n");
        builder.append("});");
        return builder.toString();
    }
}
