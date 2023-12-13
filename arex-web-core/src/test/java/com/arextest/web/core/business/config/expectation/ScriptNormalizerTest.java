package com.arextest.web.core.business.config.expectation;

import com.arextest.web.core.business.config.expectation.assertion.EqualsHandler;
import com.arextest.web.model.contract.contracts.config.expectation.ExpectationScriptModel;
import com.arextest.web.model.contract.contracts.config.expectation.ScriptExtractOperationModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @since 2023/12/15
 */
class ScriptNormalizerTest {
    static ScriptNormalizer scriptNormalizer;
    static List<ScriptAssertHandler> scriptAssertHandlerList;

    @BeforeEach
    void setUp() {
        scriptAssertHandlerList = new ArrayList<>(1);
        scriptAssertHandlerList.add(new EqualsHandler());
        scriptNormalizer = new ScriptNormalizer(scriptAssertHandlerList);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void normalize() {
        String script = "  var serviceConsumerA = arex.SoaConsumer[\"HelloService.ConsumerA\"];\n"
            + "  arex.assert.equals(\"serviceConsumerB\", serviceConsumerA.request.name);\n"
            + "\n"
            + "  var serviceConsumerB = arex.SoaConsumer[\"HelloService.ConsumerB\"];\n"
            + "  arex.assert.equals(\"serviceConsumerB\", serviceConsumerB.request.name);\n"
            + "  \n"
            + "  arex.assert.equals(\"mark4\", arex.SoaProvider[\"HelloService\"].response.order.id);";
        ExpectationScriptModel expectationScript = new ExpectationScriptModel();
        expectationScript.setAppId("test-service");
        expectationScript.setOperationId("test-operation-id");
        expectationScript.setContent(script);
        expectationScript.setNormalizedContent(expectationScript.getContent());
        boolean result = scriptNormalizer.normalize(expectationScript);
        assert result;

        assert expectationScript.getExtractOperationList().size() == 3;

        for (ScriptExtractOperationModel operation : expectationScript.getExtractOperationList()) {
            System.out.println(operation);
        }
    }
}
