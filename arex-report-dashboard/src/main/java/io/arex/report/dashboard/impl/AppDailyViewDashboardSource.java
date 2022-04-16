package io.arex.report.dashboard.impl;

import io.arex.report.dashboard.core.DashboardSource;
import lombok.Data;

import java.util.Date;


@Data
public class AppDailyViewDashboardSource implements DashboardSource {
    private String appId;
    private double percentOfPass;
    private Date date;
    private int recordedCount;
    private int replayedCount;
    private int serviceCount;
    private int operationCount;
}
