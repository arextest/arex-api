package com.arextest.web.model.dao.mongodb;

import java.util.Map;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "FSFolder")
public class FSFolderCollection extends ModelBase {

  private String name;
  private String workspaceId;
  private String parentId;
  private Integer parentNodeType;
  private Map<String, Object> customTags;
}
