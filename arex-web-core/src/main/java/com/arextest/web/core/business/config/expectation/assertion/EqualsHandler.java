package com.arextest.web.core.business.config.expectation.assertion;

import com.arextest.web.core.business.config.expectation.ScriptAssertHandler;
import com.arextest.web.model.contract.contracts.config.expectation.ScriptExtractAssertModel;
import com.arextest.web.model.contract.contracts.config.expectation.ExpectationScriptModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @since 2023/11/29
 */
@Component
public class EqualsHandler implements ScriptAssertHandler {
    // arex\.assert\.equals\((?<expected>\"\w+\"), *(?<service>\w+)\.request.(?<path>.*)\);
    private static final String EQUALS_REGEX = "arex\\.assert\\.equals\\((?<expected>\"\\w+\"), *(?<service>.+)\\.(?:request|response)\\.(?<path>.*)\\);";
    private static final Pattern EQUALS_PATTERN = Pattern.compile(EQUALS_REGEX);

    // arex\.(?<category>[a-zA-Z]+){1}\[\"(?<operation>.+)\"]
    private static final String OPERATION_REGEX = "arex\\.(?<category>[a-zA-Z]+){1}\\[\"(?<operation>.+)\"]";
    private static final Pattern OPERATION_PATTERN = Pattern.compile(OPERATION_REGEX);

    @Override
    public boolean support(ScriptExtractAssertModel model) {
        return model.getOriginalText().startsWith("arex.assert.equals");
    }

    @Override
    public void handle(ExpectationScriptModel scriptModel, ScriptExtractAssertModel assertModel) {
        Matcher equalsMatcher = EQUALS_PATTERN.matcher(assertModel.getOriginalText());
        if (!equalsMatcher.find()) {
            return;
        }
        assertModel.setMethodName("equals");
        assertModel.setExpected(equalsMatcher.group("expected"));
        assertModel.setShortServiceName(equalsMatcher.group("service"));
        assertModel.setPath(equalsMatcher.group("path"));

        if (StringUtils.startsWith(assertModel.getShortServiceName(), "arex")) {
            Matcher operationMatcher = OPERATION_PATTERN.matcher(assertModel.getShortServiceName());
            if (operationMatcher.find()) {
                assertModel.setCategoryName(operationMatcher.group("category"));
                assertModel.setOperationName(operationMatcher.group("operation"));
            }
        }

        if (StringUtils.isNotEmpty(assertModel.getOperationName())) {
            return;
        }

        scriptModel.getExtractOperationList().stream()
            .filter(importsModel -> StringUtils.equals(importsModel.getVariableName(), assertModel.getShortServiceName()))
            .findFirst()
            .ifPresent(operation -> {
                assertModel.setOperationName(operation.getOperationName());
                assertModel.setCategoryName(operation.getCategoryName());
            });
    }
}
