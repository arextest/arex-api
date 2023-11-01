package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ManualReportPlan")
public class ManualReportPlanCollection extends ModelBase {

  private String reportName;
  private String operator;
}
