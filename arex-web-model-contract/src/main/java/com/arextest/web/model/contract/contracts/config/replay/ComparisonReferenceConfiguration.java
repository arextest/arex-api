package com.arextest.web.model.contract.contracts.config.replay;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComparisonReferenceConfiguration extends AbstractComparisonDetailsConfiguration {

  private List<String> pkPath;
  private List<String> fkPath;

  @Override
  public void validParameters() throws Exception {
    super.validParameters();
    if (CollectionUtils.isEmpty(pkPath)) {
      throw new Exception("listPath cannot be empty");
    }
    if (CollectionUtils.isEmpty(fkPath)) {
      throw new Exception("keys cannot be empty");
    }
  }
}
