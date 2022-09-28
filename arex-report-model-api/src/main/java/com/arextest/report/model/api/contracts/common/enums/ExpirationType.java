package com.arextest.report.model.api.contracts.common.enums;

import lombok.Getter;

/**
 * @author jmo
 * @since 2022/2/7
 */
public enum ExpirationType {
    /**
     * pinned forever use it
     */
    PINNED_NEVER_EXPIRED(0),
    /**
     * after timeout,it would be expired
     */
    ABSOLUTE_TIME_EXPIRED(1);
    @Getter
    private final int codeValue;

    ExpirationType(int codeValue) {
        this.codeValue = codeValue;
    }
}
