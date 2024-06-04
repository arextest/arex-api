package com.arextest.web.core.business.ai.impls;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.arextest.web.core.business.ai.AIConstants;
import com.arextest.web.core.business.ai.AIProvider;
import com.arextest.web.model.contract.contracts.vertexai.GenReq;
import com.arextest.web.model.dto.vertexai.TestScriptGenRes;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.ChatResponseMessage;
import com.azure.core.http.HttpClient;
import com.azure.identity.ClientSecretCredentialBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: QizhengMo
 * @date: 2024/3/22 16:04
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "arex.ai", name = "provider", havingValue = "gpt")
public class ChatGPTProvider implements AIProvider {
  private final String model;
  private final Integer maxToken;
  private final OpenAIClient client;

  public ChatGPTProvider(@Value("${arex.ai.gpt.tenantId}") String tenantId,
      @Value("${arex.ai.gpt.clientId}") String clientId,
      @Value("${arex.ai.gpt.clientSecret}") String clientSecret,
      @Value("${arex.ai.gpt.endpoint}") String endpoint,
      @Value("${arex.ai.gpt.model}") String model,
      @Value("${arex.ai.gpt.maxToken}") Integer maxToken,
      Optional<HttpClient> httpClient) {
    this.model = model;
    this.maxToken = maxToken;
    client = getClient(clientId, tenantId, clientSecret, endpoint, httpClient.orElse(null));
  }

  private OpenAIClient getClient(String clientId,
      String tenantId,
      String clientSecret,
      String endpoint,
      HttpClient httpClient) {
    ClientSecretCredentialBuilder credential = new ClientSecretCredentialBuilder()
        .clientId(clientId)
        .tenantId(tenantId)
        .clientSecret(clientSecret);
    if (httpClient != null) {
      credential.httpClient(httpClient);
    }
    OpenAIClientBuilder builder = new OpenAIClientBuilder()
        .credential(credential.build())
        .endpoint(endpoint);
    if (httpClient != null) {
      builder.httpClient(httpClient);
    }
    return builder.buildClient();
  }

  @Override
  public TestScriptGenRes generateScripts(GenReq genReq) {
    try {
      return handle(genReq, false);
    } catch (Exception e) {
      LOGGER.error("generateScripts error on first attempt", e);

      try {
        return handle(genReq, true);
      } catch (Exception e1) {
        LOGGER.error("generateScripts error on final attempt", e1);
        TestScriptGenRes res = new TestScriptGenRes();
        res.setExplanation("System reaching rate limit, please try again later");
        return res;
      }
    }
  }

  private TestScriptGenRes handle(GenReq req, boolean trySafe) throws RuntimeException {
    try {
      StopWatch watch = new StopWatch();
      List<ChatRequestMessage> chatMessages = new ArrayList<>();
      chatMessages.add(new ChatRequestSystemMessage(AIConstants.CONTEXT_PROMPT));
      chatMessages.add(new ChatRequestUserMessage(AIConstants.USER_Q_1));
      chatMessages.add(new ChatRequestAssistantMessage(AIConstants.AI_A_1));

      if (trySafe) {
        chatMessages.add(new ChatRequestUserMessage(AIConstants.SAFE_RES_REQUIREMENT));
        chatMessages.add(new ChatRequestAssistantMessage(AIConstants.SAFE_RES_ASS_RES));
      }
      chatMessages.add(new ChatRequestUserMessage(AIConstants.MAPPER.writeValueAsString(req)));

      ChatCompletionsOptions opt = new ChatCompletionsOptions(chatMessages);
      if (maxToken != null) {
        opt.setMaxTokens(maxToken);
      }

      watch.start();
      ChatCompletions chatCompletions = client.getChatCompletions(model, opt);
      watch.stop();
      LOGGER.info("generateScripts cost: {} ms", watch.getTime());

      ChatResponseMessage completion = chatCompletions.getChoices().get(0).getMessage();
      return AIConstants.MAPPER.readValue(completion.getContent(), TestScriptGenRes.class);
    } catch (Exception e) {
      LOGGER.error("generateScripts error", e);
      throw new RuntimeException(e);
    }
  }
}
