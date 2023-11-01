package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author b_yu
 * @since 2023/1/18
 */
@Data
@Document(collection = "FSTraceLog")
public class FSTraceLogCollection extends ModelBase {

  private int traceType;
  private String userName;
  private String workspaceId;
  private String infoId;
  private String parentId;
  private String node;
  private String items;
}
