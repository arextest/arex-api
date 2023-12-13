package com.arextest.web.core.business.config.expectation;

import com.arextest.web.model.contract.contracts.config.expectation.ExpectationScriptModel;
import com.arextest.web.model.contract.contracts.config.expectation.ScriptExtractAssertModel;
import com.arextest.web.model.contract.contracts.config.expectation.ScriptExtractOperationModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @since 2023/11/29
 */
@Component
public class ScriptNormalizer {
    // (?:let (?<variable>\w+) = )?arex\.(?<category>[a-zA-Z]+){1}\[\"(?<operation>.+)\"]
    private static final String EXTRACT_OPERATION_REGEX = "(?:var (?<variable>\\w+) = )?arex\\.(?<category>[a-zA-Z]+){1}\\[\"(?<operation>.+)\"]";
    private static final Pattern EXTRACT_OPERATION_PATTERN = Pattern.compile(EXTRACT_OPERATION_REGEX);
    // arex\.assert\.[a-zA-Z]+\(.*\);
    private static final String EXTRACT_ASSERT_REGEX = "arex\\.assert\\.[a-zA-Z]+\\(.*\\);";
    private static final Pattern EXTRACT_ASSERT_PATTERN = Pattern.compile(EXTRACT_ASSERT_REGEX);

    private final List<ScriptAssertHandler> assertHandlerList;

    public ScriptNormalizer(List<ScriptAssertHandler> assertHandlerList) {
        this.assertHandlerList = assertHandlerList;
    }

    public boolean normalize(ExpectationScriptModel model) {
        model.setNormalizedContent(model.getContent());
        List<ScriptExtractOperationModel> extactOpeartionList = extactOpeartionList(model.getNormalizedContent());

        if (extactOpeartionList.isEmpty()) {
            return false;
        }

        model.setExtractOperationList(extactOpeartionList);

        // match script with EXTRACT_ASSERT_PATTERN
        Matcher assertMatcher = EXTRACT_ASSERT_PATTERN.matcher(model.getNormalizedContent());
        while (assertMatcher.find()) {
            ScriptExtractAssertModel assertModel = new ScriptExtractAssertModel(assertMatcher.group());
            for (ScriptAssertHandler handler : assertHandlerList) {
                if (!handler.support(assertModel)) {
                    continue;
                }
                handler.handle(model, assertModel);
                if (!assertModel.validate()) {
                    continue;
                }
                model.setNormalizedContent(StringUtils.replace(model.getNormalizedContent(), assertModel.getOriginalText(),
                    assertModel.rebuild()));
            }
            if (!assertModel.validate()) {
                model.setInvalidExtractAssertList(Collections.singletonList(assertModel));
                return false;
            }
        }

        return true;
    }

    private List<ScriptExtractOperationModel> extactOpeartionList(String script) {
        List<ScriptExtractOperationModel> operationList = new ArrayList<>();
        Matcher matcher = EXTRACT_OPERATION_PATTERN.matcher(script);
        while (matcher.find()) {
            ScriptExtractOperationModel operationModel = new ScriptExtractOperationModel();
            operationModel.setVariableName(matcher.group("variable"));
            operationModel.setCategoryName(matcher.group("category"));
            operationModel.setOperationName(matcher.group("operation"));
            operationModel.setOriginalText(matcher.group());
            if (operationModel.validate() && !operationList.contains(operationModel)) {
                operationList.add(operationModel);
            }
        }
        return operationList;
    }
}
