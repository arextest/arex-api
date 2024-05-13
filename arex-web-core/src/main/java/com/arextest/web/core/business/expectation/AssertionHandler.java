package com.arextest.web.core.business.expectation;

import com.arextest.web.model.contract.contracts.expectation.ScriptAssertionModel;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptModel;
import java.util.regex.Pattern;

/**
 * @since 2023/11/29
 */
public interface AssertionHandler {
    // arex\.(?<category>[a-zA-Z]+){1}\[\"(?<operation>.+)\"]
    static final String OPERATION_REGEX = "arex\\.(?<category>[a-zA-Z]+){1}\\[\"(?<operation>.+)\"]";
    static final Pattern OPERATION_PATTERN = Pattern.compile(OPERATION_REGEX);

    boolean support(ScriptAssertionModel model);
    void handle(ExpectationScriptModel scriptModel, ScriptAssertionModel assertionModel);
}
