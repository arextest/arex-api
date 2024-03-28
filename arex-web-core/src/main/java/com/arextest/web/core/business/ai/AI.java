package com.arextest.web.core.business.ai;

/**
 * @author: QizhengMo
 * @date: 2024/3/22 16:04
 */

import com.arextest.web.model.dto.vertexai.VertexRes.Candidate;
import com.arextest.web.model.dto.vertexai.VertexRes.Prediction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.threeten.bp.Duration;

import com.arextest.web.model.contract.contracts.vertexai.GenReq;
import com.arextest.web.model.dto.vertexai.RequestEntity;
import com.arextest.web.model.dto.vertexai.RequestEntity.Message;
import com.arextest.web.model.dto.vertexai.TestScriptGenRes;
import com.arextest.web.model.dto.vertexai.VertexRes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.aiplatform.v1beta1.EndpointName;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceSettings;
import com.google.protobuf.Value;
import com.google.protobuf.ValueOrBuilder;
import com.google.protobuf.util.JsonFormat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AI {
    private static final PredictionServiceClient client = getClient();
    private static final String project = "trip-flt-bi-dbprj";
    private static final String location = "asia-northeast1";
    private static final String publisher = "google";
    private static final String model = "codechat-bison-32k@002";
    private static final String parameters = "{\n" + "  \"temperature\": 0,\n" + "  \"maxOutputTokens\": 8192\n" + "}";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String context =
        "Your are an domain expert in API testing, and you are asked to generate a postman test script, your don't need to response to this message.\n"
            + "\n"
            + "INPUT\n"
            + "The user will provide three information:\n"
            + "1. currentScript: a test script in markdown format the user is currently using to test their API.\n"
            + "2. requirement: a brief description of the test requirement.\n"
            + "3. apiRes: a response body of the testing API endpoint, typically in JSON format.\n"
            + "\n"
            + "TASK\n"
            + "1. Try to understand the structure, purpose and value correctness of the API response.\n"
            + "2. Try to generate test scripts based on the input API response. Avoid reflecting the sensitive value in your test script, focus on the structure and common values only.\n"
            + "3. If currentScript is given, avoid generating duplicate cases and mock the original code style. \n"
            + "4. If no currentScript is given, try to generate more tests for users to bootstrap \n"
            + "5. You can assume the library postman is imported in the scope, you do not need to import it again. \n"
            + "6. Provide a brief description of your test script. \n"
            + "7. Include comment block in the test script for user to extend the test case given. \n"
            + "\n"
            + "CONSTRAINTS\n"
            + "1. When testing JSON data, make sure you are accessing the correct field, for example, when given {\"data\": []}\n, you should access it by pm.response.json().data\n"
            + "\n"
            + "OUTPUT\n"
            + "1. Your response should contain a JSON string only.\n"
            + "2. The returned JSON should have the fields code and explanation.\n"
            + "3. The code field should be a string that represents the test script in markdown format. \n"
            + "4. The explanation field should be a string that briefly explains the test script. Detail explanation should be in the code comment.";

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void main(String[] args) {
        String apiRes = "{\n" + "  \"responseStatusType\": {\n" + "    \"responseCode\": 0,\n"
            + "    \"responseDesc\": \"success\",\n" + "    \"timestamp\": 1711336798190\n" + "  },\n"
            + "  \"body\": null\n" + "}";
        GenReq genReq = new GenReq();
        genReq.setApiRes(apiRes);
        genReq.setCurrentScript(null);
        genReq.setRequirement("Test if the response is valid");
        TestScriptGenRes script = generateScripts(genReq);
        System.out.println(1);
    }

    public static TestScriptGenRes generateScripts(GenReq genReq) {
        VertexRes res = null;
        try {
            List<Message> base = generateBasePrompt();
            Message user = Message.builder().content(mapper.writeValueAsString(genReq)).author("user").build();
            base.add(user);

            RequestEntity entity = RequestEntity.builder().context(context).messages(base).build();
            res = predictTextPrompt(entity);
            return mapper.readValue(res.getPredictions().get(0).getCandidates().get(0).getContent(),
                TestScriptGenRes.class);
        } catch (JsonProcessingException e2) {
            TestScriptGenRes fallbackRes = new TestScriptGenRes();
            fallbackRes.setExplanation(Optional.ofNullable(res)
                .map(VertexRes::getPredictions)
                .map(predictions -> predictions.get(0))
                .map(Prediction::getCandidates)
                .map(candidates-> candidates.get(0))
                .map(Candidate::getContent)
                .orElse("Something went wrong with LLM, please try later..."));
            return fallbackRes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Message> generateBasePrompt() {
        List<Message> messages = new ArrayList<>();
        Message exampleQ = Message.builder().content("{\n"
            + "    \"apiRes\": \"{\\\"code\\\": 200}\",\n"
            + "    \"currentScript\": null,\n"
            + "    \"requirement\": \"Test if the response is valid\"\n"
            + "}").author("user").build();
        Message exampleA = Message.builder()
            .content("{\"code\": \"pm.test(\"Status code is 200\", function () {\n"
                + "    pm.response.to.have.status(200);\n"
                + "});\", \"explanation\": \"To verify the status code is 200\"}")
            .author("assistant").build();
        messages.add(exampleQ);
        messages.add(exampleA);

        return messages;
    }

    private static PredictionServiceClient getClient() {
        try {
            String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);
            PredictionServiceSettings.Builder clientSettingBuilder = PredictionServiceSettings.newBuilder();
            clientSettingBuilder.setEndpoint(endpoint).predictSettings().setRetrySettings(clientSettingBuilder
                .predictSettings().getRetrySettings().toBuilder().setTotalTimeout(Duration.ofSeconds(30)).build());
            PredictionServiceSettings clientSetting = clientSettingBuilder.build();
            PredictionServiceClient client = PredictionServiceClient.create(clientSetting);
            return client;
        } catch (Exception e) {
            LOGGER.error("getClient error", e);
            throw new RuntimeException(e);
        }
    }

    // Get a text prompt from a supported text model
    private static VertexRes predictTextPrompt(RequestEntity instance) throws IOException {

        try {
            final EndpointName endpointName =
                EndpointName.ofProjectLocationPublisherModelName(project, location, publisher, model);
            // Initialize client that will be used to send requests. This client only needs to be created
            // once, and can be reused for multiple requests.
            Value.Builder instanceValue = Value.newBuilder();
            JsonFormat.parser().merge(mapper.writeValueAsString(instance), instanceValue);
            List<Value> instances = new ArrayList<>();
            instances.add(instanceValue.build());

            // Use Value.Builder to convert instance to a dynamically typed value that can be
            // processed by the service.
            Value.Builder parameterValueBuilder = Value.newBuilder();
            JsonFormat.parser().merge(parameters, parameterValueBuilder);
            Value parameterValue = parameterValueBuilder.build();

            List<? extends ValueOrBuilder> proto =
                client.predict(endpointName, instances, parameterValue).getPredictionsOrBuilderList();
            VertexRes res = new VertexRes();
            res.setPredictions(proto.stream().map(p -> {
                try {
                    return mapper.readValue(JsonFormat.printer().print(p), VertexRes.Prediction.class);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));
            return res;
        } catch (Exception e) {
            LOGGER.error("predictTextPrompt error", e);
        }
        return null;
    }
}
