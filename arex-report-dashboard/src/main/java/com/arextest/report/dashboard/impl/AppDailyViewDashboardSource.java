package com.arextest.report.dashboard.impl;

import com.arextest.report.dashboard.core.DashboardSource;
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
