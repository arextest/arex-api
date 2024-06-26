package com.arextest.web.model.dto.filesystem;

import java.util.List;
import lombok.Data;

@Data
public class BodyDto {

  private String contentType;
  private String body;
  private List<FormDataDto> formData;
}