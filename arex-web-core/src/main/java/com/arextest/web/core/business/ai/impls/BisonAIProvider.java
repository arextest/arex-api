package com.arextest.web.core.business.ai.impls;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.threeten.bp.Duration;

import com.arextest.web.core.business.ai.AIConstants;
import com.arextest.web.core.business.ai.AIProvider;
import com.arextest.web.model.contract.contracts.vertexai.GenReq;
import com.arextest.web.model.dto.vertexai.RequestEntity;
import com.arextest.web.model.dto.vertexai.RequestEntity.Message;
import com.arextest.web.model.dto.vertexai.TestScriptGenRes;
import com.arextest.web.model.dto.vertexai.VertexRes;
import com.arextest.web.model.dto.vertexai.VertexRes.Candidate;
import com.arextest.web.model.dto.vertexai.VertexRes.Prediction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.aiplatform.v1beta1.EndpointName;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceSettings;
import com.google.protobuf.Value;
import com.google.protobuf.ValueOrBuilder;
import com.google.protobuf.util.JsonFormat;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: QizhengMo
 * @date: 2024/3/22 16:04
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "arex.ai", name = "provider", havingValue = "bison")
public class BisonAIProvider implements AIProvider {
  private static final String location = "asia-northeast1";
  private static final String publisher = "google";
  private static final String model = "codechat-bison-32k@002";
  private static final String parameters = "{\n" + "  \"temperature\": 0,\n" + "  \"maxOutputTokens\": 8192\n" + "}";
  private final PredictionServiceClient client = getClient();

  private final String projectId;

  public BisonAIProvider(
      @org.springframework.beans.factory.annotation.Value("${arex.ai.bison.projectId}") String projectId) {
    this.projectId = projectId;
  }

  private static List<Message> generateBasePrompt() {
    List<Message> messages = new ArrayList<>();
    Message exampleQ = Message.builder().content(AIConstants.USER_Q_1).author("user").build();
    Message exampleA = Message.builder().content(AIConstants.AI_A_1).author("assistant").build();
    messages.add(exampleQ);
    messages.add(exampleA);

    return messages;
  }

  private PredictionServiceClient getClient() {
    try {
      String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);
      PredictionServiceSettings.Builder clientSettingBuilder = PredictionServiceSettings.newBuilder();
      clientSettingBuilder
          .setEndpoint(endpoint)
          .predictSettings()
          .setRetrySettings(clientSettingBuilder
          .predictSettings()
              .getRetrySettings()
              .toBuilder()
              .setTotalTimeout(Duration.ofSeconds(30))
              .build());
      PredictionServiceSettings clientSetting = clientSettingBuilder.build();
      PredictionServiceClient client = PredictionServiceClient.create(clientSetting);
      return client;
    } catch (Exception e) {
      LOGGER.error("getClient error", e);
      throw new RuntimeException(e);
    }
  }

  // Get a text prompt from a supported text model
  private VertexRes predictTextPrompt(RequestEntity instance) {

    try {
      final EndpointName endpointName = EndpointName
          .ofProjectLocationPublisherModelName(projectId, location, publisher, model);
      // Initialize client that will be used to send requests. This client only needs to be created
      // once, and can be reused for multiple requests.
      Value.Builder instanceValue = Value.newBuilder();
      JsonFormat.parser().merge(AIConstants.MAPPER.writeValueAsString(instance), instanceValue);
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
          return AIConstants.MAPPER.readValue(JsonFormat.printer().print(p), VertexRes.Prediction.class);
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

  public TestScriptGenRes generateScripts(GenReq genReq) {
    VertexRes res = null;
    try {
      List<Message> base = generateBasePrompt();
      Message user =
          Message.builder().content(AIConstants.MAPPER.writeValueAsString(genReq)).author("user").build();
      base.add(user);

      RequestEntity entity = RequestEntity.builder().context(AIConstants.CONTEXT_PROMPT).messages(base).build();
      res = predictTextPrompt(entity);
      return AIConstants.MAPPER.readValue(res.getPredictions().get(0).getCandidates().get(0).getContent(),
          TestScriptGenRes.class);
    } catch (JsonProcessingException e2) {
      TestScriptGenRes fallbackRes = new TestScriptGenRes();
      fallbackRes.setExplanation(
          Optional.ofNullable(res).map(VertexRes::getPredictions).map(predictions -> predictions.get(0))
              .map(Prediction::getCandidates).map(candidates -> candidates.get(0)).map(Candidate::getContent)
              .orElse("Something went wrong with LLM, please try later..."));
      return fallbackRes;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
