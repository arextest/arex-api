package com.arextest.web.core.business.ai.impls.gemini;

import com.arextest.web.model.contract.contracts.vertexai.ModelInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.arextest.web.core.business.ai.AIConstants;
import com.arextest.web.core.business.ai.AIProvider;
import com.arextest.web.model.contract.contracts.vertexai.GenReq;
import com.arextest.web.model.dto.vertexai.TestScriptGenRes;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: QizhengMo
 * @date: 2024/3/22 16:04
 */
@Slf4j
@Service
@ConditionalOnExpression("#{systemProperties['arex.ai.providers'].contains('gemini1')}")
@EnableConfigurationProperties(GeminiConfig.class)
public class GeminiProvider implements AIProvider {
  private static final String location = "asia-northeast1";
  private final GenerativeModel client;

  private final String modelName;
  private final String projectId;
  private final Integer maxToken;

  private final ModelInfo modelInfo = new ModelInfo();

  public GeminiProvider(GeminiConfig config) {
    this.projectId = config.getProjectId();
    this.modelName = config.getModelName();
    this.maxToken = config.getMaxToken() == null ? 0 : config.getMaxToken();

    this.client = getClient();

    // init model info
    this.modelInfo.setModelName(this.client.getModelName());
    this.modelInfo.setTokenLimit(this.client.getGenerationConfig().getMaxOutputTokens());
  }

  private GenerativeModel getClient() {
    try {
      VertexAI vertexAI = new VertexAI(projectId, location);
      GenerativeModel model = new GenerativeModel(modelName, vertexAI);
      model.withGenerationConfig(GenerationConfig.newBuilder()
          .setTemperature(0.3F)
          .setMaxOutputTokens(maxToken)
          .setResponseMimeType("application/json")
          .build());
      return model;
    } catch (Exception e) {
      LOGGER.error("getClient error", e);
      throw new RuntimeException(e);
    }
  }

  private GenerateContentResponse gen(String prompt) throws IOException {
    List<Content> prompts = getBasePrompts();
    prompts.add(Content.newBuilder()
        .setRole("user")
        .addParts(Part.newBuilder().setText(prompt))
        .build());
    return client.generateContent(prompts);
  }

  private static List<Content> getBasePrompts() {
    List<Content> prompts = new ArrayList<>();
    prompts.add(Content.newBuilder()
        .setRole("user")
        .addParts(Part.newBuilder().setText(AIConstants.CONTEXT_PROMPT))
        .build());
    prompts.add(Content.newBuilder().setRole("model")
        .addParts(Part.newBuilder()
            .setText("Sure, I understand your requirement and will help generating test scripts for you."))
        .build());
    prompts.add(
        Content.newBuilder()
            .setRole("user")
            .addParts(Part.newBuilder().setText(AIConstants.USER_Q_1))
            .build());
    prompts
        .add(Content.newBuilder()
            .setRole("model").addParts(Part.newBuilder().setText(AIConstants.AI_A_1))
            .build());
    return prompts;
  }

  @Override
  public TestScriptGenRes generateScripts(GenReq genReq) {
    try {
      GenerateContentResponse genRes = gen(AIConstants.MAPPER.writeValueAsString(genReq));
      String text = genRes.getCandidates(0).getContent().getParts(0).getText();
      try {
        // remove ```json and ``` from the response
        text = text.contains("```") ? text.substring(7, text.length() - 3) : text;
      } catch (Exception ignore) {
      }

      return AIConstants.MAPPER.readValue(text, TestScriptGenRes.class);
    } catch (Exception e) {
      LOGGER.error("generateScripts error", e);
      TestScriptGenRes res = new TestScriptGenRes();
      res.setExplanation("Sorry, I can't generate test scripts for you now. Please try again later.");
      return res;
    }
  }

  @Override
  public @NonNull ModelInfo getModelInfo() {
    return this.modelInfo;
  }
}
