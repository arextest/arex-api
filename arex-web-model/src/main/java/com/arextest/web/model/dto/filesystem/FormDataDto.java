package com.arextest.web.model.dto.filesystem;

import lombok.Data;

/**
 * @author b_yu
 * @since 2024/6/26
 */
@Data
public class FormDataDto {
  private String key;
  private String value;
  private String type;
}
