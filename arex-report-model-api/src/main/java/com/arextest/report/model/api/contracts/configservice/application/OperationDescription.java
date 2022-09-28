package com.arextest.report.model.api.contracts.configservice.application;

import com.arextest.report.model.api.contracts.common.enums.ApplicationServiceOperationType;

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
