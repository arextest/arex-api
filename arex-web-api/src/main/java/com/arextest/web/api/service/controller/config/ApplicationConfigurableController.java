package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.JwtUtil;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.config.model.dto.application.ApplicationConfiguration;
import com.arextest.model.replay.AppVisibilityLevelEnum;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ScheduleConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/application")
public class ApplicationConfigurableController extends
    AbstractConfigurableController<ApplicationConfiguration> {

  @Resource
  private ScheduleConfigurableHandler scheduleHandler;

  public ApplicationConfigurableController(
      @Autowired ConfigurableHandler<ApplicationConfiguration> configurableHandler) {
    super(configurableHandler);
  }

  @GetMapping("/regressionList")
  @ResponseBody
  public Response regressionList() {
    ServletRequestAttributes requestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = requestAttributes.getRequest();
    String accessToken = request.getHeader("access-token");
    String userName = JwtUtil.getUserName(accessToken);
    List<ApplicationConfiguration> sourceList = this.configurableHandler.useResultAsList();
    sourceList = sourceList.stream()
        .filter(applicationConfiguration -> visibilityCheck(applicationConfiguration, userName))
        .collect(Collectors.toList());
    Map<String, ScheduleConfiguration> scheduleMap = scheduleHandler.useResultAsList().stream()
        .collect(
            Collectors.toMap(ScheduleConfiguration::getAppId, Function.identity(),
                (oldValue, newValue) -> newValue));

    List<ApplicationRegressionView> viewList = new ArrayList<>(sourceList.size());
    ApplicationRegressionView view;
    for (ApplicationConfiguration application : sourceList) {
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

  private boolean visibilityCheck(ApplicationConfiguration applicationConfiguration,
      String userName) {
    return applicationConfiguration.getVisibilityLevel()
        == AppVisibilityLevelEnum.ALL_VISIBLE.getCode()
        || (applicationConfiguration.getVisibilityLevel()
        == AppVisibilityLevelEnum.OWNER_VISIBLE.getCode()
        && applicationConfiguration.getOwners().contains(userName));
  }

  @Data
  private static final class ApplicationRegressionView {

    private ApplicationConfiguration application;
    private ScheduleConfiguration regressionConfiguration;
  }
}
