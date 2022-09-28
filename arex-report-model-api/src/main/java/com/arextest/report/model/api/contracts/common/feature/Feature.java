package com.arextest.report.model.api.contracts.common.feature;

/**
 * @author jmo
 * @since 2022/1/21
 */
public interface Feature {
    String name();

    boolean enabledIn(int flags);

    int getMask();
}
