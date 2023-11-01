package com.arextest.web.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wildeslam.
 * @create 2023/9/8 14:16
 */
@AllArgsConstructor
public enum FeedbackTypeEnum {
  UNKNOWN(0), BUG(1), BY_DESIGN(2), AREX_PROBLEM(3);;

  @Getter
  private final Integer code;

  public static FeedbackTypeEnum from(int code) {
    for (FeedbackTypeEnum type : FeedbackTypeEnum.values()) {
      if (type.getCode() == code) {
        return type;
      }
    }
    return UNKNOWN;
  }
}
