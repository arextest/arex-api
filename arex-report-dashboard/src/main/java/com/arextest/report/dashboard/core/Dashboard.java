package com.arextest.report.dashboard.core;

import java.time.Duration;
import java.util.Date;
import java.util.List;


public interface Dashboard<T extends DashboardSource> {
    String getTitle();

    String getDescription();

    List<T> sourceList();

    interface OverView extends DashboardSource {
        int appCount();

        int serviceCount();

        int operationCount();

        int recordedCount();
    }

    interface DailyOverView extends DashboardSource {

        double percentOfPass();

        int appCount();

        Date date();
    }


    interface ReportHistory {
        int id();

        String appId();

        String reportName();

        int caseCount();

        int successCount();

        int failCount();

        int exceptionCount();

        double percentOfPass();

        Duration replayDuration();

        String createBy();

        String createdTime();
    }
}
