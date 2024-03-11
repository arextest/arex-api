package com.arextest.web.core.business.config.application;

import com.arextest.config.model.dto.application.InstancesConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.config.repository.impl.InstancesConfigurationRepositoryImpl;
import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Slf4j
@Component
public final class ApplicationInstancesConfigurableHandler extends
    AbstractConfigurableHandler<InstancesConfiguration> {

  @Resource
  private InstancesConfigurationRepositoryImpl instancesConfigurationRepository;

  protected ApplicationInstancesConfigurableHandler(
      @Autowired ConfigRepositoryProvider<InstancesConfiguration> repositoryProvider) {
    super(repositoryProvider);
  }

  public void createOrUpdate(InstancesConfiguration instancesConfiguration) {
    super.update(instancesConfiguration);
  }

  private void create(InstancesConfiguration instancesConfiguration) {
    super.insert(instancesConfiguration);
  }

  public boolean deleteByAppIdAndHost(String appId, String host) {
    return instancesConfigurationRepository.removeByAppIdAndHost(appId, host);
  }
}