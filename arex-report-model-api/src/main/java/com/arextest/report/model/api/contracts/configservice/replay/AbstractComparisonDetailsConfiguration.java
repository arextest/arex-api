package com.arextest.report.model.api.contracts.configservice.replay;


import com.arextest.report.model.api.contracts.common.enums.ExpirationType;
import com.arextest.report.model.api.contracts.configservice.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public abstract class AbstractComparisonDetailsConfiguration extends AbstractConfiguration {

    private String id;

    /**
     * optional
     * if empty should be apply to all appIds replay compare,means global default
     */
    private String appId;

    /**
     * optional
     * The value limit to special operation should be used, else,couldn't apply for it.
     * empty,means is unlimited.
     */
    private String operationId;

    /**
     * the value from {@link ExpirationType} indicate which type should be used.
     */
    private int expirationType;
    private Date expirationDate;

}
