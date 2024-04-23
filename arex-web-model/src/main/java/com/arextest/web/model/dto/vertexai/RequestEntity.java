package com.arextest.web.model.dto.vertexai;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/3/25 11:03
 */
@Data
@Builder
public class RequestEntity {
  private String context;
  private List<Message> messages;
  @Data
  @Builder
  public static class Message {
    private String content;
    private String author;
  }
}