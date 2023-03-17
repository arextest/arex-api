package com.arextest.web.model.contract.contracts.config.application;

import com.arextest.web.model.contract.contracts.config.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * created by xinyuan_wang on 2023/3/14
 */
@Getter
@Setter
public class InstancesConfiguration extends AbstractConfiguration {
    private String id;
    private String appId;
    private String recordVersion;
    private String host;
    private Date dataUpdateTime;
}
