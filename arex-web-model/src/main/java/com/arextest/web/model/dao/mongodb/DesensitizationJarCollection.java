package com.arextest.web.model.dao.mongodb;

import java.util.Date;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author qzmo
 * @since 2023/8/16
 */
@Data
@Document(collection = "DesensitizationJar")
public class DesensitizationJarCollection extends ModelBase {

  private String jarUrl;
  private String remark;
  @Deprecated
  private Date uploadDate;
}
