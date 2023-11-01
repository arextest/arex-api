package com.arextest.web.model.contract.contracts.config.replay;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComparisonExclusionsConfiguration extends AbstractComparisonDetailsConfiguration {

  List<String> exclusions;

  @Override
  public void validParameters() throws Exception {
    super.validParameters();
    if (CollectionUtils.isEmpty(exclusions)) {
      throw new Exception("exclusions cannot be empty");
    }
  }
}