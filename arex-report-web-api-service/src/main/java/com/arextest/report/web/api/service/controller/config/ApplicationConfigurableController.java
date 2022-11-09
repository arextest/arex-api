package com.arextest.report.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.config.ConfigurableHandler;
import com.arextest.report.model.api.contracts.config.application.ApplicationConfiguration;
import com.arextest.report.model.api.contracts.config.replay.ScheduleConfiguration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/application")
public final class ApplicationConfigurableController extends AbstractConfigurableController<ApplicationConfiguration> {

    @Resource
    private ConfigurableHandler<ScheduleConfiguration> scheduleHandler;

    public ApplicationConfigurableController(@Autowired ConfigurableHandler<ApplicationConfiguration> configurableHandler) {
        super(configurableHandler);
    }

    @GetMapping("/regressionList")
    @ResponseBody
    public Response regressionList() {
        List<ApplicationConfiguration> source = this.configurableHandler.useResultAsList();
        List<ApplicationRegressionView> viewList = new ArrayList<>(source.size());
        ApplicationRegressionView view;
        for (ApplicationConfiguration application : source) {
            view = new ApplicationRegressionView();
            view.setApplication(application);
            view.setRegressionConfiguration(scheduleHandler.useResult(application.getAppId()));
            viewList.add(view);
        }
        return ResponseUtils.successResponse(viewList);
    }


    @Data
    private static final class ApplicationRegressionView {
        private ApplicationConfiguration application;
        private ScheduleConfiguration regressionConfiguration;
    }
}
