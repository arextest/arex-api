package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import java.util.Collection;
import lombok.Data;

/**
 * Created by rchen9 on 2022/9/27.
 */
@Data
public class OperationCompareTemplateConfig {

  private String operationName;

  private Collection<String> exclusions;
  private Collection<String> inclusions;
  private Collection<ListSortTemplateConfig> listSort;
  private Collection<ReferenceTemplateConfig> references;
}
