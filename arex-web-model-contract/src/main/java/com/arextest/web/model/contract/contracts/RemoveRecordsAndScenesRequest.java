package com.arextest.web.model.contract.contracts;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2023/8/16 16:43
 */
@Data
public class RemoveRecordsAndScenesRequest {

  // key: actionId, value: recordIds
  private Map<String, List<String>> actionIdAndRecordIdsMap;
}
