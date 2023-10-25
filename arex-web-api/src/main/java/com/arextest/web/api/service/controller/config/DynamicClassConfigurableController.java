package com.arextest.web.api.service.controller.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arextest.common.model.response.Response;
import com.arextest.config.model.dto.ModifyType;
import com.arextest.config.model.dto.record.DynamicClassConfiguration;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.record.ServiceCollectConfigurableHandler;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/dynamicClass")
public class DynamicClassConfigurableController extends AbstractConfigurableController<DynamicClassConfiguration> {
    @Resource
    private ServiceCollectConfigurableHandler serviceCollectConfigurableHandler;

    public DynamicClassConfigurableController(
        @Autowired ConfigurableHandler<DynamicClassConfiguration> configurableHandler) {
        super(configurableHandler);
    }

    @Override
    @ResponseBody
    public Response modify(@PathVariable ModifyType modifyType, @RequestBody DynamicClassConfiguration configuration)
        throws Exception {
        // change dataChangeUpdatesTime in recordServiceConfig before modifying
        serviceCollectConfigurableHandler.updateServiceCollectTime(configuration.getAppId());

        return super.modify(modifyType, configuration);
    }
}
