package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ScheduleConfigurableHandler;
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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/application")
public final class ApplicationConfigurableController extends AbstractConfigurableController<ApplicationConfiguration> {

    @Resource
    private ScheduleConfigurableHandler scheduleHandler;

    public ApplicationConfigurableController(
            @Autowired ConfigurableHandler<ApplicationConfiguration> configurableHandler) {
        super(configurableHandler);
    }

    @GetMapping("/regressionList")
    @ResponseBody
    public Response regressionList() {
        List<ApplicationConfiguration> sourceMap = this.configurableHandler.useResultAsList();
        Map<String, ScheduleConfiguration> scheduleMap = scheduleHandler.useResultAsList()
                .stream()
                .collect(Collectors.toMap(ScheduleConfiguration::getAppId,
                        Function.identity(),
                        (oldValue, newValue) -> newValue));

        List<ApplicationRegressionView> viewList = new ArrayList<>(sourceMap.size());
        ApplicationRegressionView view;
        for (ApplicationConfiguration application : sourceMap) {
            view = new ApplicationRegressionView();
            view.setApplication(application);
            ScheduleConfiguration configuration = scheduleMap.get(application.getAppId());
            if (configuration == null) {
                configuration = scheduleHandler.createFromGlobalDefault(application.getAppId()).get(0);
            }
            view.setRegressionConfiguration(configuration);
            viewList.add(view);
        }
        return ResponseUtils.successResponse(viewList);
    }

    @PostMapping("/removeHosts")
    @ResponseBody
    public Response removeAgentMachines(@Valid @RequestBody RemoveAgentMachinesRequest request) {
        ApplicationConfiguration app = this.configurableHandler.useResult(request.getAppId());
        RemoveAgentMachinesResponse response = new RemoveAgentMachinesResponse();
        if (CollectionUtils.isEmpty(app.getHosts())) {
            response.setSuccess(true);
            return ResponseUtils.successResponse(response);
        }
        if (CollectionUtils.isEmpty(request.getHosts())) {
            app.getHosts().clear();
        } else {
            app.getHosts().removeAll(request.getHosts());
        }
        response.setSuccess(this.configurableHandler.update(app));
        return ResponseUtils.successResponse(response);
    }

    @Data
    private static final class RemoveAgentMachinesRequest {
        @NotNull(message = "AppId cannot be empty")
        private String appId;
        private Set<String> hosts;
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
