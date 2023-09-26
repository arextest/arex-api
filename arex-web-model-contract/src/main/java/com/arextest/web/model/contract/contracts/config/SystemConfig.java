package com.arextest.web.model.contract.contracts.config;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2023/9/25 16:57
 */
@Data
public class SystemConfig {
    /**
     * @see com.arextest.web.model.enums.SystemConfigTypeEnum
     */
    private Integer configType;

    private String operator;

    /**
     * for callBackInform.
     */
    private String callbackUrl;
}
