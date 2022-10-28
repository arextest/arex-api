package com.arextest.report.model.api.contracts.config.replay;

import com.arextest.report.model.api.contracts.config.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * @author jmo
 * @since 2021/12/21
 */
@Getter
@Setter
public class ScheduleConfiguration extends AbstractConfiguration {
    private String appId;
    /**
     * the dependent operations should be skipped when replaying
     * the exclusion of operations: "/api/order/get/1111": []
     * the exclusion of db/redis: "htlorderidmdb_dalcluster": ["update", "query"]
     */
    private Map<String, Set<String>> excludeOperationMap;
    /**
     * 默认回放Case范围
     */
    private Integer offsetDays;

    /**
     * 默认回放环境
     */
    private Set<String> targetEnv;
    private Integer sendMaxQps;

}
