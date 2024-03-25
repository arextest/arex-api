package com.arextest.web.core.business.ai;

/**
 * @author: QizhengMo
 * @date: 2024/3/22 16:04
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private static final String model = "codechat-bison@002";
    private static final String parameters = "{\n" + "  \"temperature\": 0,\n" + "  \"maxOutputTokens\": 2048\n" + "}";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String context =
        "The user will provide a prompt in JSON format, containing three fields: "
            + "apiRes: a response of an API endpoint "
            + "currentScript: a test script in markdown format "
            + "requirement: a brief description of the test requirement. "

            + "Try to understand the meaning of the response and generate a postman test script. "
            + "If the currentScript is given, generate a small test snippet that can be added to the current script, generate full test script otherwise. "
            + "The library postman is imported in the scope as an variable named arex, you do not need to import it again. "

            + "Your response should contain a JSON string only, the returned JSON should have the fields code and explanation."
            + "The code field should be a string that represents the test script in markdown format. "
            + "The explanation field should be a string that briefly explains the test script. Detail explanation should be in the code comment.";

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
        try {
            List<Message> base = generateBasePrompt();
            Message user = Message.builder().content(mapper.writeValueAsString(genReq)).author("user").build();
            base.add(user);

            RequestEntity entity = RequestEntity.builder().context(context).messages(base).build();
            VertexRes res = predictTextPrompt(entity);
            return mapper.readValue(res.getPredictions().get(0).getCandidates().get(0).getContent(),
                TestScriptGenRes.class);
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
            .content("{\"code\": \"arex.test(\"Status code is 200\", function () {\n"
                + "    arex.response.to.have.status(200);\n"
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
