package com.arextest.web.model.contract.contracts.expectation;

import java.util.Objects;
import lombok.Data;

/**
 * @since 2023/11/29
 */
@Data
public class ScriptExtractOperationModel {
    String categoryName;
    String variableName;
    String operationName;
    String originalText;

    public boolean validate() {
        return categoryName != null && operationName != null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        ScriptExtractOperationModel that = (ScriptExtractOperationModel) object;

        if (!Objects.equals(categoryName, that.categoryName)) {
            return false;
        }
        return Objects.equals(operationName, that.operationName);
    }

    @Override
    public int hashCode() {
        int result = categoryName != null ? categoryName.hashCode() : 0;
        result = 31 * result + (operationName != null ? operationName.hashCode() : 0);
        return result;
    }
}
