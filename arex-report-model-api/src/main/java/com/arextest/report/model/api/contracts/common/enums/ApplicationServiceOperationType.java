package com.arextest.report.model.api.contracts.common.enums;

import lombok.Getter;

/**
 * @author jmo
 * @since 2022/2/11
 */
public enum ApplicationServiceOperationType {

    /**
     * The http Servlet entry
     */
    HTTP_SERVLET_SERVICE(15);

    @Getter
    private final int codeValue;

    ApplicationServiceOperationType(int codeValue) {
        this.codeValue = codeValue;
    }
}
