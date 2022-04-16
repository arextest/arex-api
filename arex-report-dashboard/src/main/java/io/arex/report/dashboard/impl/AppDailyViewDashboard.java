package io.arex.report.dashboard.impl;

import io.arex.report.dashboard.core.Dashboard;

import java.util.ArrayList;
import java.util.List;


public class AppDailyViewDashboard implements Dashboard<AppDailyViewDashboardSource> {
    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<AppDailyViewDashboardSource> sourceList() {
        List<AppDailyViewDashboardSource> sources = new ArrayList<>();

        return sources;
    }
}
