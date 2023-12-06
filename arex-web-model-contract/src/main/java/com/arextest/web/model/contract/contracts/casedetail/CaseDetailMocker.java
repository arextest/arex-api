package com.arextest.web.model.contract.contracts.casedetail;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.model.mock.Mocker;
import java.util.Map;
import lombok.Data;

/**
 * @Author qzmo
 * @Date 2023/12/06
 */
@Data
public class CaseDetailMocker {
  private String id;
  private MockCategoryType categoryType;
  private String replayId;
  private String recordId;
  private String appId;
  private int recordEnvironment;
  private long creationTime;
  private long updateTime;
  private long expirationTime;
  private CaseDetailTarget targetRequest;
  private CaseDetailTarget targetResponse;
  private String operationName;
  private String recordVersion;

  @Data
  public static class CaseDetailTarget {
    private Object body;
    private Map<String, Object> attributes;
    private String type;
  }
}
