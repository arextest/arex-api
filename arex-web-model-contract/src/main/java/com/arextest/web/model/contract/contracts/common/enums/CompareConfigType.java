package com.arextest.web.model.contract.contracts.common.enums;

import lombok.Getter;

/**
 * @author jmo
 * @since 2022/2/7
 */
public enum CompareConfigType {
    /**
     * the config of comparison, which is main entrance
     */
    REPLAY_MAIN(0),
    /**
     * the config of comparison, which is collection
     */
    COLLECTION(1),
    /**
     * the config of comparison, which is dependency
     */
    REPLAY_DEPENDENCY(2);

    @Getter
    private final int codeValue;

    CompareConfigType(int codeValue) {
        this.codeValue = codeValue;
    }
}
