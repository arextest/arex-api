package com.arextest.web.model.contract.contracts.config.replay;

import com.arextest.web.model.contract.contracts.compare.TransformDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComparisonTransformConfiguration extends AbstractComparisonDetailsConfiguration {

  TransformDetail transformDetail;

  @Override
  public void validParameters() throws Exception {
    super.validParameters();
    if (transformDetail == null || CollectionUtils.isEmpty(transformDetail.getNodePath())) {
      throw new Exception("nodePath cannot be empty");
    }
  }


}