package com.arextest.report.model.dao.mongodb.entity;

import lombok.Data;

@Data
public class KeyValuePairDao {
    private String key;
    private String value;
    private Boolean active;
}
