package com.arextest.web.core.business.config.record;

import com.arextest.config.model.dto.record.ServiceCollectConfiguration;
import com.arextest.config.repository.MultiEnvConfigRepositoryProvider;
import com.arextest.config.repository.impl.ServiceCollectConfigurationRepositoryImpl;
import com.arextest.web.core.business.config.AbstractMultiEnvConfigHandler;
import java.util.Collections;
import java.util.List;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Slf4j
@Component
public final class ServiceCollectConfigurableHandler
    extends AbstractMultiEnvConfigHandler<ServiceCollectConfiguration> {

  @Resource
  ServiceCollectConfigurationRepositoryImpl serviceCollectConfigurationRepository;

  protected ServiceCollectConfigurableHandler(
      @Autowired MultiEnvConfigRepositoryProvider<ServiceCollectConfiguration> repositoryProvider) {
    super(repositoryProvider);
  }

  @Override
  protected List<ServiceCollectConfiguration> createFromGlobalDefault(String appId) {
    LOGGER.error("query and find serviceCollectConfiguration is empty, appId:{}", appId);
    return Collections.emptyList();
  }

  @Override
  public boolean update(ServiceCollectConfiguration configuration) {
    return super.update(configuration) || super.insert(configuration);
  }

  @Override
  protected void mergeGlobalDefaultSettings(ServiceCollectConfiguration source) {
  }

  public void updateServiceCollectTime(String appId) {
    serviceCollectConfigurationRepository.updateServiceCollectTime(appId);
  }
}
