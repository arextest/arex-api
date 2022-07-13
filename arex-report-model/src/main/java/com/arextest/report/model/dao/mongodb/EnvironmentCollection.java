package com.arextest.report.model.dao.mongodb;

import com.arextest.report.model.dao.mongodb.entity.KeyValuePairDao;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "Environment")
public class EnvironmentCollection extends ModelBase {
    private String workspaceId;
    private String envName;
    private List<KeyValuePairDao> keyValues;
}
