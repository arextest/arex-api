package com.arextest.web.model.dao.mongodb;

import java.util.Map;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

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


  public interface KeySummary {

    // to identify the refresh task
    String REFRESH_DATA = "refresh_data";

  }
}