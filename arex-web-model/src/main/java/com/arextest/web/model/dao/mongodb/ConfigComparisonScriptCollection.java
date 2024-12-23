package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = "ConfigComparisonScript")
public class ConfigComparisonScriptCollection extends AbstractComparisonDetails {


  private List<String> nodePath;

  private ScriptMethodDao scriptMethod;


  @Data
  public static class ScriptMethodDao {

    private String functionId;
    private String aliasName;
    private String functionArgs;
  }


}
