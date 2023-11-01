package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Data
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = "ConfigComparisonReference")
public class ConfigComparisonReferenceCollection extends AbstractComparisonDetails {

  private List<String> pkPath;
  private List<String> fkPath;
}
