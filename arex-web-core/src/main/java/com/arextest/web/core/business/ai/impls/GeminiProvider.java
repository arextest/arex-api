package com.arextest.web.core.business.ai.impls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(prefix = "arex.ai", name = "provider", havingValue = "gemini")
public class GeminiProvider implements AIProvider {
  private static final String location = "asia-northeast1";
  private static final String modelName = "gemini-1.0-pro";
  private static final GenerativeModel client = getClient();

  private static GenerativeModel getClient() {
    try {
      VertexAI vertexAI = new VertexAI(AIConstants.GOOGLE_PROJECT_ID, location);
      GenerativeModel model = new GenerativeModel(modelName, vertexAI);
      return model;
    } catch (Exception e) {
      LOGGER.error("getClient error", e);
      throw new RuntimeException(e);
    }
  }

  private static GenerateContentResponse gen(String prompt) throws IOException {
    GenerationConfig config = GenerationConfig.newBuilder().setMaxOutputTokens(8192).setTemperature(0.5F).build();
    List<Content> prompts = getBasePrompts();
    prompts.add(Content.newBuilder()
        .setRole("user")
        .addParts(Part.newBuilder().setText(prompt))
        .build());
    return client.generateContent(prompts, config);
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
      return AIConstants.MAPPER.readValue(genRes.getCandidates(0).getContent().getParts(0).getText(),
          TestScriptGenRes.class);
    } catch (Exception e) {
      LOGGER.error("generateScripts error", e);
      TestScriptGenRes res = new TestScriptGenRes();
      res.setExplanation("Sorry, I can't generate test scripts for you now. Please try again later.");
      return res;
    }
  }
}
