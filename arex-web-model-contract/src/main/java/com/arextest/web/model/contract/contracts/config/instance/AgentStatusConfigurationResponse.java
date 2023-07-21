package com.arextest.web.model.contract.contracts.config.instance;

import lombok.Data;

/**
 * @author Zhitao Gan
 * @since 2023/7/13
 */
@Data
public class AgentStatusConfigurationResponse {
    int status; // 1 representing WORKING, 2 representing SLEEPING
    long version; // the count of DynamicClass of appid + Math.max(RecordServiceConfig.dataChangeUpdateTime, DynamicClass.dataChangeUpdateTime)
}
