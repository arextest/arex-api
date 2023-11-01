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
 * @author wildeslam.
 * @create 2023/8/18 15:11
 */
@Data
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = "ConfigComparisonIgnoreCategory")
public class ConfigComparisonIgnoreCategoryCollection extends AbstractComparisonDetails {

  private List<String> ignoreCategory;
}
