package com.arextest.report.model.api.contracts.config.dashboard;

import com.arextest.report.model.api.contracts.config.application.ApplicationDescription;
import lombok.Data;

/**
 * @author jmo
 * @since 2022/1/21
 */
@Data
public class AppDashboardView {
    private ApplicationDescription applicationDescription;
    private int operationCount;
}
