package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import java.util.List;
import lombok.Data;

@Data
public class QueryMsgWithDiffResponseType extends DesensitizationResponseType {

  private String replayId;
  private String recordId;
  private int diffResultCode;
  private String baseMsg;
  private String testMsg;
  private List<LogEntity> logs;
}
