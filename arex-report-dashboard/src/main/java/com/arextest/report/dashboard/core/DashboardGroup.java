package com.arextest.report.dashboard.core;

import java.util.List;


public interface DashboardGroup {
    String getTitle();

    String getDescription();

    
    String getVisualization();

    
    String getDrawMode();

    List<Dashboard> getDashboardList();
}
