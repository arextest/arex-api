package com.arextest.web.model.contract.contracts.config;

import lombok.Data;

import java.util.Set;

/**
 * @author wildeslam.
 * @create 2024/2/20 17:45
 */
@Data
public class SystemConfigWithProperties extends SystemConfiguration {
    /**
     * control the compare precision of the time field.
     */
    private Long compareIgnoreTimePrecisionMillis;
    /**
     * ignore the case, when comparing
     */
    private Boolean compareNameToLower;
    /**
     * the null and '' think unanimously, when comparing
     */
    private Boolean compareNullEqualsEmpty;

    /**
     * according to the names of node to ignore the node.
     */
    private Set<String> ignoreNodeSet;

    /**
     * skip the compare of select, when comparing database.
     */
    private Boolean selectIgnoreCompare;

    /**
     * only compare the coincident columns, when comparing database.
     */
    private Boolean onlyCompareCoincidentColumn;

    /**
     * ignore the compare of uuid
     */
    private Boolean uuidIgnore;

    /**
     * ignore the compare of uuid
     */
    private Boolean ipIgnore;
}
