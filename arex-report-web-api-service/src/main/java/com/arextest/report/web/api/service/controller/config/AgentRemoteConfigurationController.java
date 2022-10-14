package com.arextest.report.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.config.application.ApplicationServiceConfigurableHandler;
import com.arextest.report.core.business.config.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.common.enums.StatusType;
import com.arextest.report.model.api.contracts.config.application.ApplicationConfiguration;
import com.arextest.report.model.api.contracts.config.record.DynamicClassConfiguration;
import com.arextest.report.model.api.contracts.config.record.ServiceCollectConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author jmo
 * @since 2022/1/22
 */
@Slf4j
@Controller
@RequestMapping("/api/config/agent")
public final class AgentRemoteConfigurationController {
    @Resource
    private ConfigurableHandler<DynamicClassConfiguration> dynamicClassHandler;
    @Resource
    private ConfigurableHandler<ApplicationConfiguration> applicationHandler;
    @Resource
    private ConfigurableHandler<ServiceCollectConfiguration> serviceCollectHandler;
    @Resource
    private ApplicationServiceConfigurableHandler applicationServiceHandler;
    private ScheduledExecutorService executorService;
    @Value("${arex.config.application.service.update.delaySeconds:30}")
    private long delayUpdateServiceSeconds;

    @PostConstruct
    private void init() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @PostMapping("/load")
    @ResponseBody
    public Response load(@RequestBody AgentRemoteConfigurationRequest request) {
        final String appId = request.getAppId();
        if (StringUtils.isEmpty(appId)) {
            return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
        }
        LOGGER.info("from appId: {} , load config", request.appId);
        ApplicationConfiguration applicationConfiguration = this.loadApplicationResult(request);
        if (applicationConfiguration == null) {
            LOGGER.info("from appId: {} load config resource not found", request.appId);
            return ResponseUtils.resourceNotFoundResponse();
        }
        ServiceCollectConfiguration serviceCollectConfiguration = serviceCollectHandler.useResult(appId);
        applicationServiceHandler.createOrUpdate(request.getAppId());
        AgentRemoteConfigurationResponse body = new AgentRemoteConfigurationResponse();
        body.setDynamicClassConfigurationList(dynamicClassHandler.useResultAsList(appId));
        body.setServiceCollectConfiguration(serviceCollectConfiguration);
        body.setStatus(applicationConfiguration.getStatus());
        return ResponseUtils.successResponse(body);
    }

    private void delayUpdateApplicationService(AgentRemoteConfigurationRequest request) {
        executorService.schedule(new UpdateApplicationRunnable(request), delayUpdateServiceSeconds, TimeUnit.SECONDS);
    }

    private final class UpdateApplicationRunnable implements Runnable {
        private final AgentRemoteConfigurationRequest request;

        private UpdateApplicationRunnable(AgentRemoteConfigurationRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            try {
                applicationServiceHandler.createOrUpdate(request.getAppId());
            } catch (Throwable e) {
                LOGGER.error("update application service error:{}", e.getMessage(), e);
            }
        }
    }

    private ApplicationConfiguration loadApplicationResult(AgentRemoteConfigurationRequest request) {
        ApplicationConfiguration applicationConfiguration = applicationHandler.useResult(request.getAppId());
        return applicationConfiguration;
    }

    @Data
    private static final class AgentRemoteConfigurationRequest {
        private String appId;
    }

    @Data
    private static final class AgentRemoteConfigurationResponse {
        private ServiceCollectConfiguration serviceCollectConfiguration;
        private List<DynamicClassConfiguration> dynamicClassConfigurationList;

        /**
         * Bit flag composed of bits that record/replay are enabled.
         * see {@link  StatusType }
         */
        private Integer status;
    }
}
