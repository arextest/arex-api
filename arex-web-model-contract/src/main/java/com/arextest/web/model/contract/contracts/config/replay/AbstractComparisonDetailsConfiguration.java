package com.arextest.web.model.contract.contracts.config.replay;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.arextest.web.model.contract.contracts.common.enums.CompareConfigType;
import com.arextest.web.model.contract.contracts.common.enums.ExpirationType;
import com.arextest.web.model.contract.contracts.config.AbstractConfiguration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractComparisonDetailsConfiguration extends AbstractConfiguration {

    String id;

    /**
     * optional when compareConfigType = 1, appId is empty
     */
    String appId;

    /**
     * optional The value limit to special operation should be used, else,couldn't apply for it. if it is empty, it
     * means is the global configuration of app That the configuration of app have the meaning of global is "Exclusion"
     */
    String operationId;

    /**
     * the value from {@link ExpirationType} indicate which type should be used.
     */
    int expirationType;
    Date expirationDate;

    /**
     * the source of the configuration. {@link CompareConfigType}
     */
    int compareConfigType;

    /**
     * This value is valid only when {compareConfigType} = 1
     */
    String fsInterfaceId;

    /**
     * for bo
     */
    String dependencyId;

    /**
     * for vo
     */
    String operationType;
    String operationName;

    @Override
    public void validParameters() throws Exception {
        super.validParameters();

        // not allow appId and operationId and interfaceId all be empty
        if (StringUtils.isEmpty(appId) && StringUtils.isEmpty(operationId) && StringUtils.isEmpty(fsInterfaceId)) {
            throw new Exception("appId, operationId and interfaceId cannot be empty at the same time");
        }
    }

}
