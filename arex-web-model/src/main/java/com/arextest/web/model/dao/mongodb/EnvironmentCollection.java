package com.arextest.web.model.dao.mongodb;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.arextest.web.model.dao.mongodb.entity.KeyValuePairDao;

import lombok.Data;

@Data
@Document(collection = "Environment")
public class EnvironmentCollection extends ModelBase {
    private String workspaceId;
    private String envName;
    private List<KeyValuePairDao> keyValues;
}
