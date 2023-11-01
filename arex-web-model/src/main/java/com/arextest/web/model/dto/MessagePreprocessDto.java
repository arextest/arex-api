package com.arextest.web.model.dto;

import lombok.Data;

@Data
public class MessagePreprocessDto {

  private String key;
  private String path;
  private Long publishDate;
}
