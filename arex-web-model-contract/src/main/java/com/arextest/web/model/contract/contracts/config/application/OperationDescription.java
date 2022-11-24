package com.arextest.web.model.contract.contracts.config.application;

import com.arextest.web.model.contract.contracts.common.enums.ApplicationServiceOperationType;

/**
 * @author jmo
 * @since 2021/12/21
 */
public interface OperationDescription {
    String getOperationName();

    /**
     * {@link  ApplicationServiceOperationType}
     *
     * @return a code value
     */
    int getOperationType();
}
