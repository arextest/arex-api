package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import com.arextest.config.model.dto.DesensitizationJar;
import com.arextest.config.model.dto.SystemConfiguration;
import com.arextest.config.repository.impl.SystemConfigurationRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * Created by Qzmo on 2023/8/16
 */
@SuppressWarnings("squid:S5122")
@Controller
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/api/desensitization/", produces = "application/json;charset=UTF-8")
public class DataDesensitizationController {

    @Resource
    private SystemConfigurationRepositoryImpl systemConfigurationRepository;

    @PostMapping("/listJar")
    @ResponseBody
    public Response listJar() {
        SystemConfiguration systemConfiguration = systemConfigurationRepository.getSystemConfigByKey(
            SystemConfigurationCollection.KeySummary.DESERIALIZATION_JAR);
        List<DesensitizationJar> desensitizationJarList =
            Collections.singletonList(systemConfiguration.getDesensitizationJar());
        return ResponseUtils.successResponse(desensitizationJarList);
    }
}