package com.arextest.web.model.contract.contracts.common.enums;

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
    ABSOLUTE_TIME_EXPIRED(1),
    /**
     * after timeout, still displayed but not work.
     */
    SOFT_TIME_EXPIRED(2),
    ;

    @Getter
    private final int codeValue;

    ExpirationType(int codeValue) {
        this.codeValue = codeValue;
    }
}
