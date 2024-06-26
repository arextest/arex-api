package com.arextest.web.model.dao.mongodb.entity;

import java.util.List;
import lombok.Data;

@Data
public class BodyDao {

  private String contentType;
  private String body;
  private List<FormDataDao> formData;
}
