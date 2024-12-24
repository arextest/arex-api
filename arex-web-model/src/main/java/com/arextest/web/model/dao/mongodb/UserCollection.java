package com.arextest.web.model.dao.mongodb;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@FieldNameConstants
@Document(collection = "User")
public class UserCollection extends ModelBase {
  private String userName;
  private String verificationCode;
  private Long verificationTime;
  private String profile;
  private List<String> favoriteApps;
  private Integer status;
  private List<Activity> activities;

  @Data
  public static class Activity {
    private Date date;
    private String type;
  }
}
