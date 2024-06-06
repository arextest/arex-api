package com.arextest.web.core.business.ai.impls.gpt;

import com.arextest.web.core.business.ai.impls.gpt.OpenAICompletionReq.Message;
import java.util.Arrays;

import com.arextest.web.core.business.ai.AIConstants;
import com.arextest.web.core.business.ai.AIProvider;
import com.arextest.web.model.contract.contracts.vertexai.GenReq;
import com.arextest.web.model.contract.contracts.vertexai.ModelInfo;
import com.arextest.web.model.dto.vertexai.TestScriptGenRes;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Lists;

import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: QizhengMo
 * @date: 2024/3/22 16:04
 */
@Slf4j
abstract public class ChatGPTProvider implements AIProvider {
  private final String model;
  private final String authToken;
  private final Integer maxToken;
  private final ModelInfo modelInfo = new ModelInfo();

  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final HttpRequestFactory REQ_FACTORY = HTTP_TRANSPORT.createRequestFactory();
  private final GenericUrl COMPLETION_URI;

  private static final String SYSTEM_ROLE = "system";
  private static final String USER_ROLE = "user";
  private static final String ASS_ROLE = "assistant";

  public ChatGPTProvider(TokenGPTConfig config) {
    this.model = config.getModel();
    this.maxToken = config.getMaxToken();
    this.authToken = config.getToken();
    this.getModelInfo().setModelName(model);
    this.getModelInfo().setTokenLimit(maxToken);
    this.COMPLETION_URI = new GenericUrl(config.getEndpoint() + "/chat/completions");
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
      OpenAICompletionReq openAiReq = new OpenAICompletionReq();
      openAiReq.setModel(model);
      openAiReq.setMessages(getBasePrompt());

      if (trySafe) {
        openAiReq.getMessages().add(new OpenAICompletionReq.Message(SYSTEM_ROLE, AIConstants.SAFE_RES_REQUIREMENT));
      }
      // current User Req
      openAiReq.getMessages().add(new OpenAICompletionReq.Message(USER_ROLE, AIConstants.MAPPER.writeValueAsString(req)));
      String completion = callOpenAI(openAiReq).getChoices().get(0).getMessage().getContent();
      return AIConstants.MAPPER.readValue(completion, TestScriptGenRes.class);
    } catch (Exception e) {
      return null;
    }
  }

  private static List<Message> getBasePrompt() {
    return Lists.newArrayList(Arrays.asList(
        new Message(SYSTEM_ROLE, AIConstants.CONTEXT_PROMPT),
        new Message(USER_ROLE, AIConstants.USER_Q_1),
        new Message(ASS_ROLE, AIConstants.AI_A_1)
    ));
  }

  private OpenAICompletionRes callOpenAI(OpenAICompletionReq req) {
    try {
      HttpContent content = new ByteArrayContent("application/json", AIConstants.MAPPER.writeValueAsBytes(req));
      HttpRequest request = REQ_FACTORY.buildPostRequest(COMPLETION_URI, content);
      request.setReadTimeout(5 * 60 * 1000);
      request.setHeaders(buildHeaders());
      HttpResponse response = request.execute();
      return AIConstants.MAPPER.readValue(response.getContent(), OpenAICompletionRes.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to call OpenAI", e);
    }
  }

  private HttpHeaders buildHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + this.authToken);
    return headers;
  }

  @Override
  public @NonNull ModelInfo getModelInfo() {
    return modelInfo;
  }
}
