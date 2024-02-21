package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

import java.util.Map;

/**
 * @author wildeslam.
 * @create 2024/2/20 20:12
 */
@Data
public class RefreshTask {
    /**
     * The problem of prohibiting concurrent repeated insertions, the key is unique the function of
     * this record
     */
    private Map<String, Integer> refreshTaskMark;
}
