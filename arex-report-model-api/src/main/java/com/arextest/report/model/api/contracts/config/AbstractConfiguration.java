package com.arextest.report.model.api.contracts.config;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Getter
@Setter
public abstract class AbstractConfiguration {
    private Integer status;
    private Timestamp modifiedTime;
}
