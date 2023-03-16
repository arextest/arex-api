package com.arextest.web.api.service.controller.config;

import com.arextest.web.core.business.config.application.ApplicationInstancesConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.application.InstancesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by rchen9 on 2022/9/30.
 */
@Controller
@RequestMapping("/api/config/applicationInstances")
public class ApplicationInstancesConfigurableController extends AbstractConfigurableController<InstancesConfiguration> {

    public ApplicationInstancesConfigurableController(@Autowired ApplicationInstancesConfigurableHandler configurableHandler) {
        super(configurableHandler);
    }

}