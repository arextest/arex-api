package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author b_yu
 * @since 2024/12/18
 */
@FieldNameConstants
@Data
public class CountResult {
  private long total;
}
