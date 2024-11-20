package com.arextest.web.model.dto;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class UserDto {
  private String id;
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
    private ActivityType type;
  }

  public enum ActivityType {
    LOGIN, LOGOUT, REFRESH_TOKEN
  }
}
