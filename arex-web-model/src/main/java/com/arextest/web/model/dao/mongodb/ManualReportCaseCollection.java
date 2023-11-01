package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.AuthDao;
import com.arextest.web.model.dao.mongodb.entity.BodyDao;
import com.arextest.web.model.dao.mongodb.entity.KeyValuePairDao;
import com.arextest.web.model.dao.mongodb.entity.ScriptBlockDao;
import java.util.List;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ManualReportCase")
public class ManualReportCaseCollection extends ModelBase {

  private List<ScriptBlockDao> preRequestScripts;
  private List<ScriptBlockDao> testScripts;
  private BodyDao body;
  private List<KeyValuePairDao> headers;
  private List<KeyValuePairDao> params;
  private AuthDao auth;

  private String planItemId;
  private String caseName;
  private String baseMsg;
  private String testMsg;
  private String logs;
  private Integer diffResultCode;
}
