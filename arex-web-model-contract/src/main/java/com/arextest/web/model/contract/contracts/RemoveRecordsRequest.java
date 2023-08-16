package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wildeslam.
 * @create 2023/8/16 16:43
 */
@Data
public class RemoveRecordsRequest {
    // key: actionId, value: recordIds
    private Map<String, List<String>> actionIdAndRecordIdsMap;
}
