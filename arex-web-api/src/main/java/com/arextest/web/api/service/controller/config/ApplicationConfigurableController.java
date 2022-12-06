package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.application.ApplicationConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/application")
public final class ApplicationConfigurableController extends AbstractConfigurableController<ApplicationConfiguration> {

    @Resource
    private ConfigurableHandler<ScheduleConfiguration> scheduleHandler;

    public ApplicationConfigurableController(
            @Autowired ConfigurableHandler<ApplicationConfiguration> configurableHandler) {
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

    @PostMapping("/removeIps")
    @ResponseBody
    public Response removeAgentMachines(@Valid @RequestBody RemoveAgentMachinesRequest request) {
        ApplicationConfiguration app = this.configurableHandler.useResult(request.getAppId());
        RemoveAgentMachinesResponse response = new RemoveAgentMachinesResponse();
        if (CollectionUtils.isEmpty(app.getIps())) {
            response.setSuccess(true);
            return ResponseUtils.successResponse(response);
        }
        if (CollectionUtils.isEmpty(request.getIps())) {
            app.getIps().clear();
        } else {
            app.getIps().removeAll(request.getIps());
        }
        response.setSuccess(this.configurableHandler.update(app));
        return ResponseUtils.successResponse(response);
    }

    @Data
    private static final class RemoveAgentMachinesRequest {
        @NotNull(message = "AppId cannot be empty")
        private String appId;
        private Set<String> ips;
    }


    @Data
    private static final class RemoveAgentMachinesResponse {
        private boolean success;
    }


    @Data
    private static final class ApplicationRegressionView {
        private ApplicationConfiguration application;
        private ScheduleConfiguration regressionConfiguration;
    }
}
