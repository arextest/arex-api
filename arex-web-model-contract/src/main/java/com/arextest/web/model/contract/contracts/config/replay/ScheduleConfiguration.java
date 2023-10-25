package com.arextest.web.model.contract.contracts.config.replay;

import java.util.Map;
import java.util.Set;

import com.arextest.config.model.dto.AbstractConfiguration;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jmo
 * @since 2021/12/21
 */
@Getter
@Setter
public class ScheduleConfiguration extends AbstractConfiguration {
    private String appId;
    /**
     * the dependent operations should be skipped when replaying the exclusion of operations: "/api/order/get/1111": []
     * the exclusion of db/redis: "htlorderidmdb_dalcluster": ["update", "query"]
     */
    private Map<String, Set<String>> excludeOperationMap;
    /**
     * the default range of case to replay
     */
    private Integer offsetDays;

    /**
     * default replay environment
     */
    private Set<String> targetEnv;
    /**
     * send max qps
     */
    private Integer sendMaxQps;

    private Set<String> excludeServiceOperationSet;

    private Set<String> includeServiceOperationSet;

}
