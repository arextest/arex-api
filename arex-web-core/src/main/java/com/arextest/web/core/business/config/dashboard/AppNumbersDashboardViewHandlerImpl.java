package com.arextest.web.core.business.config.dashboard;

import com.arextest.web.core.business.config.ViewHandler;
import com.arextest.web.model.contract.contracts.config.application.ApplicationConfiguration;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.web.model.contract.contracts.config.dashboard.AppDashboardView;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Component
final class AppNumbersDashboardViewHandlerImpl implements ViewHandler<AppDashboardView> {
    @Resource
    private ViewHandler<ApplicationConfiguration> applicationViewHandler;
    @Resource
    private ViewHandler<ApplicationOperationConfiguration> operationViewHandler;

    @Override
    public List<AppDashboardView> useResultAsList(String appId) {
        ApplicationConfiguration application = applicationViewHandler.useResult(appId);
        if (application == null) {
            return null;
        }
        return Collections.singletonList(newDashboardView(application));
    }

    @Override
    public List<AppDashboardView> useResultAsList() {
        List<ApplicationConfiguration> applicationList = applicationViewHandler.useResultAsList();
        if (CollectionUtils.isEmpty(applicationList)) {
            return Collections.emptyList();
        }
        int applicationSize = CollectionUtils.size(applicationList);
        List<AppDashboardView> dashboardList = new ArrayList<>(applicationSize);
        for (int i = 0; i < applicationSize; i++) {
            ApplicationConfiguration application = applicationList.get(i);
            dashboardList.add(newDashboardView(application));
        }
        return dashboardList;
    }

    private AppDashboardView newDashboardView(ApplicationConfiguration application) {
        AppDashboardView appDashboardView = new AppDashboardView();
        appDashboardView.setApplicationDescription(application);
        int operationSize = operationSize(application.getAppId());
        appDashboardView.setOperationCount(operationSize);
        return appDashboardView;
    }

    private int operationSize(String appId) {
        List<?> source = operationViewHandler.useResultAsList(appId);
        return CollectionUtils.size(source);
    }
}
