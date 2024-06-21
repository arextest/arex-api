package com.arextest.web.core.business.ai.impls.gpt;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/6/6 15:41
 */
@Data
public class OpenAICompletionReq {
  public String model;
  public List<Message> messages;

  @Data
  @AllArgsConstructor
  public static class Message {
    public String role;
    public String content;
  }
}
