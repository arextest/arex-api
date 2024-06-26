package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import lombok.Data;

@Data
public class BodyType {

  private String contentType;
  private String body;
  private List<FormDataType> formData;
}