package com.arextest.web.core.business.config.expectation;

import com.arextest.web.model.contract.contracts.config.expectation.ScriptExtractAssertModel;
import com.arextest.web.model.contract.contracts.config.expectation.ExpectationScriptModel;

/**
 * @since 2023/11/29
 */
public interface ScriptAssertHandler {
    boolean support(ScriptExtractAssertModel model);
    void handle(ExpectationScriptModel scriptModel, ScriptExtractAssertModel assertModel);
}
