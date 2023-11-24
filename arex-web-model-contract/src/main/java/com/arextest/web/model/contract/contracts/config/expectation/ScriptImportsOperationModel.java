package com.arextest.web.model.contract.contracts.config.expectation;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;

/**
 * @since 2023/11/29
 */
@Data
public class ScriptImportsOperationModel {
    String categoryName;
    String variableName;
    String operationName;
    String originalText;

    // let +(?<variableName>.*) += +arex.(?<category>.*)?\[\"(?<dependencyName>.*)\"]
    private static final String REGEX = "let +(?<variableName>.*) += +arex.(?<category>.*)\n?\\[\"(?<operation>.*)\"]";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public ScriptImportsOperationModel(String originalText) {
        this.originalText = originalText;
        if (originalText == null) {
            return;
        }
        Matcher matcher = PATTERN.matcher(originalText);
        if (matcher.find()) {
            variableName = matcher.group("variableName");
            categoryName = matcher.group("category");
            operationName = matcher.group("operation");
        }
    }

    public boolean validate() {
        return variableName != null
            && categoryName != null
            && operationName != null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        ScriptImportsOperationModel that = (ScriptImportsOperationModel) object;

        if (!Objects.equals(categoryName, that.categoryName)) {
            return false;
        }
        if (!Objects.equals(variableName, that.variableName)) {
            return false;
        }
        return Objects.equals(operationName, that.operationName);
    }

    @Override
    public int hashCode() {
        int result = categoryName != null ? categoryName.hashCode() : 0;
        result = 31 * result + (variableName != null ? variableName.hashCode() : 0);
        result = 31 * result + (operationName != null ? operationName.hashCode() : 0);
        return result;
    }
}
