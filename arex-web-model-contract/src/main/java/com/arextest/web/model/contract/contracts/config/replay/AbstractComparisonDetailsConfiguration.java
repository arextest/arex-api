package com.arextest.web.model.contract.contracts.config.replay;


import java.util.Date;

import org.apache.logging.log4j.util.Strings;

import com.arextest.web.model.contract.contracts.common.enums.CompareConfigType;
import com.arextest.web.model.contract.contracts.common.enums.ExpirationType;
import com.arextest.web.model.contract.contracts.config.AbstractConfiguration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractComparisonDetailsConfiguration extends AbstractConfiguration {

    private String id;

    /**
     * optional
     * when compareConfigType = 1, appId is empty
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

    /**
     * the source of the configuration.
     * {@link CompareConfigType}
     */
    private int compareConfigType;

    /**
     * This value is valid only when {compareConfigType} = 1
     */
    private String fsInterfaceId;

    /**
     * This value is valid only when {compareConfigType} = 2
     */
    private String dependencyId;

    public void validParameters() throws Exception {
        if (Strings.isNotBlank(this.getDependencyId())){
            this.setCompareConfigType(CompareConfigType.REPLAY_DEPENDENCY.getCodeValue());
        }
    }

}
