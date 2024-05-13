package com.arextest.web.core.business.config.expectation;

import com.arextest.web.core.business.expectation.AssertionHandler;
import com.arextest.web.core.business.expectation.ScriptNormalizer;
import com.arextest.web.core.business.expectation.assertion.EqualsHandler;
import com.arextest.web.core.business.expectation.assertion.IsHandler;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptModel;
import com.arextest.web.model.contract.contracts.expectation.ScriptExtractOperationModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @since 2023/12/15
 */
class ScriptNormalizerTest {
    static ScriptNormalizer scriptNormalizer;
    static List<AssertionHandler> assertionHandlers;

    @BeforeEach
    void setUp() {
        assertionHandlers = new ArrayList<>(2);
        assertionHandlers.add(new EqualsHandler());
        assertionHandlers.add(new IsHandler());
        scriptNormalizer = new ScriptNormalizer(assertionHandlers);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void normalize() {
        String script = "var serviceConsumerA = arex.SoaConsumer[\"HelloService.ConsumerA\"];\n"
            + "arex.assert.equals(\"test equals HelloService.ConsumerA request name\", \"serviceConsumerB\", serviceConsumerA.request.name);\n"
            + "arex.assert.equals(\"test equals HelloService.ConsumerB request name\", \"serviceConsumerB\", serviceConsumerB.request.name);\n"
            + "var serviceConsumerB = arex.SoaConsumer[\"HelloService.ConsumerB\"];\n"
            + "arex.assert.notEquals(\"test notEquals HelloService.ConsumerB request name\", \"serviceConsumerB\", serviceConsumerB.request.name);\n"
            + "arex.assert.equals(\"test equals HelloService response order id\", 123, arex.SoaProvider[\"HelloService\"].response.order.id);\n"
            + "arex.assert.isNull(arex.SoaProvider[\"HelloService\"].response, \"response is null\");\n"
            + "arex.assert.isNullOrEmpty(serviceConsumerB.response.order.id, \"isNullOrEmpty test\");\n"
            + "arex.assert.isNotNullOrEmpty(arex.SoaProvider[\"HelloService\"].response.order.id, \"isNotNullOrEmpty test\");\n"
            + "arex.assert.isNull(arex.SoaProvider[\"HelloService\"].response.order.id, \"Status code is 200\");\n"
            + "arex.assert.isNotNull(arex.SoaProvider[\"HelloService\"].response.order.id, \"Response has the required Content-Type header\");\n"
            + "arex.assert.isTrue(arex.SoaProvider[\"HelloService\"].response.order.id, \"ResponseStatusType object exists and has required properties\");\n"
            + "arex.assert.isFalse(arex.SoaProvider[\"HelloService\"].response.order.id, \"isFalse test\");";
        ExpectationScriptModel expectationScript = new ExpectationScriptModel();
        expectationScript.setAppId("test-service");
        expectationScript.setOperationId("test-operation-id");
        expectationScript.setContent(script);
        expectationScript.setNormalizedContent(expectationScript.getContent());
        boolean result = scriptNormalizer.normalize(expectationScript);
        Assertions.assertTrue(result);

        StringBuilder log = new StringBuilder();
        log.append("normalized script : \n").append(expectationScript.getNormalizedContent()).append("\n\n");

        log.append("extract operation list : \n");
        for (ScriptExtractOperationModel operation : expectationScript.getExtractOperationList()) {
            log.append(operation).append("\n");
        }
        System.out.println(log);
    }
}
