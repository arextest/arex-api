package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.contract.contracts.common.DesensitizationJar;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@FieldNameConstants
@Document(collection = "SystemConfiguration")
public class SystemConfigurationCollection extends ModelBase {

  /**
   * The problem of prohibiting concurrent repeated insertions, the key is unique the function of
   * this record
   */
  private String key;
  private Map<String, Integer> refreshTaskMark;
  private DesensitizationJar desensitizationJar;
  private String callbackUrl;


  public interface KeySummary {

    // to identify the refresh task
    String REFRESH_DATA = "refresh_data";
    String DESERIALIZATION_JAR = "deserialization_jar";
    String CALLBACK_URL = "callback_url";

  }
}