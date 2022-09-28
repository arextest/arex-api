package com.arextest.report.model.api.contracts.configservice.record;

import com.arextest.report.model.api.contracts.configservice.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jmo
 * @since 2021/12/22
 */
@Getter
@Setter
public class DynamicClassConfiguration extends AbstractConfiguration {
    private String id;
    private String appId;
    private String fullClassName;
    private String methodName;
    private String parameterTypes;
    private String keyFormula;

    /**
     * from system provide or user custom provide
     */
    private int configType;

}
