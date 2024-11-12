package com.arextest.web.model.contract.contracts.config.replay;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComparisonScriptConfiguration extends AbstractComparisonDetailsConfiguration {

  private List<String> nodePath;

  private ScriptMethod scriptMethod;


  @Data
  public static class ScriptMethod {

    private String methodName;
    private String methodArgs;
  }


}
