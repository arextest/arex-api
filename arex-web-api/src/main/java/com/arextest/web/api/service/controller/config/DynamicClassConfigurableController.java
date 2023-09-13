package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.config.model.dto.ModifyType;
import com.arextest.config.model.dto.record.DynamicClassConfiguration;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.record.ServiceCollectConfigurableHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/dynamicClass")
public final class DynamicClassConfigurableController extends AbstractConfigurableController<DynamicClassConfiguration> {
    public DynamicClassConfigurableController(@Autowired ConfigurableHandler<DynamicClassConfiguration> configurableHandler) {
        super(configurableHandler);
    }

    @Resource
    private ServiceCollectConfigurableHandler serviceCollectConfigurableHandler;

    @Override
    @ResponseBody
    public Response modify(@PathVariable ModifyType modifyType, @RequestBody DynamicClassConfiguration configuration) throws Exception {
        // change dataChangeUpdatesTime in recordServiceConfig before modifying
        serviceCollectConfigurableHandler.updateServiceCollectTime(configuration.getAppId());

        return super.modify(modifyType, configuration);
    }
}
