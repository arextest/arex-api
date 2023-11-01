package com.arextest.web.model.params;

import java.util.Map;
import lombok.Data;

/**
 * @author b_yu
 * @since 2023/2/10
 */
@Data
public class QueryLogsParam {

  private Integer pageSize;

  private String previousId;
  private String level;
  private Long startTime;
  private Long endTime;
  private Map<String, String> tags;
}
