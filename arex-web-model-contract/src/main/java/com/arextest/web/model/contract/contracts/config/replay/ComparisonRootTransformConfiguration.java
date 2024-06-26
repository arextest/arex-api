package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComparisonRootTransformConfiguration extends AbstractComparisonDetailsConfiguration {

  private String transformMethodName;

  @Override
  public void validParameters() throws Exception {
    super.validParameters();
    if (StringUtils.isEmpty(transformMethodName)) {
      throw new Exception("transformMethodName cannot be empty");
    }
  }

}
