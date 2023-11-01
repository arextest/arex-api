package com.arextest.web.model.dao.mongodb.entity;

import lombok.Data;

@Data
public class ReportAppConfigSummery {

  private String name;

  private Integer appCount;

  private Integer serviceCount;

  private Integer operationCount;
}
