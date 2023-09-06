package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.application.ApplicationInstancesConfigurableHandler;
import com.arextest.web.core.business.config.record.ServiceCollectConfigurableHandler;
import com.arextest.web.model.contract.contracts.common.enums.ModifyType;
import com.arextest.web.model.contract.contracts.config.application.InstancesConfiguration;
import com.arextest.web.model.contract.contracts.config.record.ServiceCollectConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by rchen9 on 2022/9/30.
 */
@Controller
@RequestMapping("/api/config/applicationInstances")
public class ApplicationInstancesConfigurableController extends AbstractConfigurableController<InstancesConfiguration> {

    public ApplicationInstancesConfigurableController(@Autowired ApplicationInstancesConfigurableHandler configurableHandler) {
        super(configurableHandler);
    }

    @Resource
    private ServiceCollectConfigurableHandler serviceCollectConfigurableHandler;

    @Override
    @ResponseBody
    public Response modify(@PathVariable ModifyType modifyType,@RequestBody InstancesConfiguration configuration) throws Exception {
        serviceCollectConfigurableHandler.updateServiceCollectTime(configuration.getAppId());

        return super.modify(modifyType, configuration);
    }
}