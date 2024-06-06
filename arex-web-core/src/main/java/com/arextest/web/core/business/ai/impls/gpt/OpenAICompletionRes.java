package com.arextest.web.core.business.ai.impls.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/6/6 15:41
 */
@Data
public class OpenAICompletionRes {
  public String id;
  public List<Choice> choices;
  public int created;
  public String model;
  public String object;
  @JsonProperty("system_fingerprint")
  public String systemFingerprint;
  public Usage usage;

  @Data
  public static class Choice {
    @JsonProperty("finish_reason")
    public String finishReason;
    public int index;
    public Message message;
  }

  @Data
  public static class Message {
    public String content;
    public String role;
  }

  @Data
  public static class Usage {
    @JsonProperty("completion_tokens")
    public int completionTokens;
    @JsonProperty("prompt_tokens")
    public int promptTokens;
    @JsonProperty("total_tokens")
    public int totalTokens;
  }
}
