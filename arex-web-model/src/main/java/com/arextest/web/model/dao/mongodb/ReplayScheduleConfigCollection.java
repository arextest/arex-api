package com.arextest.web.model.dao.mongodb;

import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@FieldNameConstants
@Document(collection = "ReplayScheduleConfig")
public class ReplayScheduleConfigCollection extends ModelBase {

  @NonNull
  @Indexed(unique = true)
  private String appId;
  private String excludeOperationMap;
  private Integer offsetDays;
  private Set<String> targetEnv;
  private Integer sendMaxQps;
  private String mockHandlerJarUrl;
}
