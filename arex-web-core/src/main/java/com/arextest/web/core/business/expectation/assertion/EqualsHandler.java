package com.arextest.web.core.business.expectation.assertion;

import com.arextest.web.core.business.expectation.AssertionHandler;
import com.arextest.web.model.contract.contracts.expectation.ScriptAssertionModel;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @since 2023/11/29
 */
@Component
public class EqualsHandler implements AssertionHandler {
    private static final String SUPPORT_REGEX = "arex\\.assert\\.(?<method>equals|notEquals)";
    private static final Pattern SUPPORT_PATTERN = Pattern.compile(SUPPORT_REGEX);

    // arex\.assert\.(?<method>equals|notEquals)\(\"(?<testName>.*)\", (?<expected>\"?.+\"?), (?<actual>(?<service>.+)\.(?:request|response)\.(?<path>.*))\);
    private static final String EQUALS_REGEX = SUPPORT_REGEX + "\\(\"(?<testName>.*)\", (?<expected>\"?.+\"?), (?<actual>(?<service>.+)\\.(?:request|response)\\.(?<path>.*))\\)";
    private static final Pattern EQUALS_PATTERN = Pattern.compile(EQUALS_REGEX);

    @Override
    public boolean support(ScriptAssertionModel model) {
        return SUPPORT_PATTERN.matcher(model.getOriginalText()).find();
    }

    @Override
    public void handle(ExpectationScriptModel scriptModel, ScriptAssertionModel assertionModel) {
        Matcher matcher = EQUALS_PATTERN.matcher(assertionModel.getOriginalText());
        if (!matcher.find()) {
            return;
        }
        assertionModel.setTestName(matcher.group("testName"));
        assertionModel.setMethod(matcher.group("method"));
        assertionModel.setExpected(matcher.group("expected"));
        assertionModel.setActual(matcher.group("actual"));
        assertionModel.setShortServiceName(matcher.group("service"));
        assertionModel.setPath(matcher.group("path"));

        if (StringUtils.startsWith(assertionModel.getShortServiceName(), "arex")) {
            Matcher operationMatcher = OPERATION_PATTERN.matcher(assertionModel.getShortServiceName());
            if (operationMatcher.find()) {
                assertionModel.setCategory(operationMatcher.group("category"));
                assertionModel.setOperation(operationMatcher.group("operation"));
            }
        }

        if (StringUtils.isNotEmpty(assertionModel.getOperation())) {
            return;
        }

        scriptModel.getExtractOperationList().stream()
            .filter(importsModel -> StringUtils.equals(importsModel.getVariableName(), assertionModel.getShortServiceName()))
            .findFirst()
            .ifPresent(operation -> {
                assertionModel.setOperation(operation.getOperationName());
                assertionModel.setCategory(operation.getCategoryName());
            });
    }
}
