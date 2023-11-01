package com.arextest.web.model.dao.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "ManualReportPlan")
public class ManualReportPlanCollection extends ModelBase {
    private String reportName;
    private String operator;
}
