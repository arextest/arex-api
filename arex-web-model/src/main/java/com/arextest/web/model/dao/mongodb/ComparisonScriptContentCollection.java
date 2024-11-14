package com.arextest.web.model.dao.mongodb;

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
@Document(collection = "ComparisonScriptContent")
public class ComparisonScriptContentCollection extends ModelBase {

  private String aliasName;

  private String content;

}
