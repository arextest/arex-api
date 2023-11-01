package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.FSNode;
import java.util.List;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "FSTree")
public class FSTreeCollection extends ModelBase {

  private String workspaceName;
  private String userName;
  private List<FSNode> roots;
}
