package com.arextest.web.model.contract.contracts.config.replay;

import java.util.List;

import com.arextest.web.model.contract.contracts.compare.CategoryDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

/**
 * @author wildeslam.
 * @create 2023/8/18 14:39
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComparisonIgnoreCategoryConfiguration extends AbstractComparisonDetailsConfiguration {
  private CategoryDetail ignoreCategoryDetail;
}
