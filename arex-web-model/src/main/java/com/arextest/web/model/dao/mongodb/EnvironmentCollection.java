package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.KeyValuePairDao;
import java.util.List;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Environment")
public class EnvironmentCollection extends ModelBase {

  private String workspaceId;
  private String envName;
  private List<KeyValuePairDao> keyValues;
}
