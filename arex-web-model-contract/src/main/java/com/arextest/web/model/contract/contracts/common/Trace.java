package com.arextest.web.model.contract.contracts.common;

import java.util.List;
import lombok.Data;

@Data
public class Trace {

  private List<List<NodeEntity>> currentTraceLeft;
  private List<List<NodeEntity>> currentTraceRight;
}
