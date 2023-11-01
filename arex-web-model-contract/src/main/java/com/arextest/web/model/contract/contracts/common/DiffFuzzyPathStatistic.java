package com.arextest.web.model.contract.contracts.common;

import java.util.List;
import lombok.Data;

@Data
public class DiffFuzzyPathStatistic {

  private String fuzzyPath;

  private Integer caseCount;

  private Integer sceneCount;

  private Integer groupTypeId;

  private List<String> sceneIdList;
}
