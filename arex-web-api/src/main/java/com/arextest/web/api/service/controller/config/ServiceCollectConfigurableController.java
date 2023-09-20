package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.config.model.dto.record.ServiceCollectConfiguration;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.record.ServiceCollectConfigurableHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/serviceCollect")
public final class ServiceCollectConfigurableController extends AbstractConfigurableController<ServiceCollectConfiguration> {
    public ServiceCollectConfigurableController(@Autowired ConfigurableHandler<ServiceCollectConfiguration> configurableHandler) {
        super(configurableHandler);
    }
}
