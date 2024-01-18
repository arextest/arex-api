package com.arextest.web.core.business.config.record;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.arextest.config.model.dto.record.ServiceCollectConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.business.config.MultiEnvConfigurableHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Slf4j
@Component
public final class ServiceCollectConfigurableHandler
    extends AbstractConfigurableHandler<ServiceCollectConfiguration>
    implements MultiEnvConfigurableHandler<ServiceCollectConfiguration> {

  @Resource
  private ServiceCollectConfiguration globalDefaultConfiguration;

  protected ServiceCollectConfigurableHandler(
      @Autowired ConfigRepositoryProvider<ServiceCollectConfiguration> repositoryProvider) {
    super(repositoryProvider);
  }

  @Override
  protected List<ServiceCollectConfiguration> createFromGlobalDefault(String appId) {
    ServiceCollectConfiguration serviceCollectConfiguration = new ServiceCollectConfiguration();
    serviceCollectConfiguration.setAppId(appId);
    serviceCollectConfiguration.setSampleRate(globalDefaultConfiguration.getSampleRate());
    serviceCollectConfiguration.setAllowDayOfWeeks(globalDefaultConfiguration.getAllowDayOfWeeks());
    serviceCollectConfiguration.setTimeMock(globalDefaultConfiguration.isTimeMock());
    serviceCollectConfiguration.setAllowTimeOfDayFrom(
        globalDefaultConfiguration.getAllowTimeOfDayFrom());
    serviceCollectConfiguration.setAllowTimeOfDayTo(
        globalDefaultConfiguration.getAllowTimeOfDayTo());
    serviceCollectConfiguration
        .setRecordMachineCountLimit(
            globalDefaultConfiguration.getRecordMachineCountLimit() == null ? 1
                : globalDefaultConfiguration.getRecordMachineCountLimit());
    return Collections.singletonList(serviceCollectConfiguration);
  }

  @Override
  public boolean update(ServiceCollectConfiguration configuration) {
    return super.update(configuration) || super.insert(configuration);
  }

  @Override
  protected void mergeGlobalDefaultSettings(ServiceCollectConfiguration source) {
  }

  @Override
  protected boolean shouldMergeGlobalDefault() {
    return true;
  }

  private <T> Set<T> mergeValues(Set<T> source, Set<T> globalValues) {
    if (CollectionUtils.isEmpty(globalValues)) {
      return source;
    }
    if (CollectionUtils.isEmpty(source)) {
      return globalValues;
    }
    source.addAll(globalValues);
    return source;
  }

  public void updateServiceCollectTime(String appId) {
    ServiceCollectConfiguration serviceCollectConfiguration = this.useResult(appId);
    this.update(serviceCollectConfiguration);
  }

  @Override
  public boolean editMultiEnvList(ServiceCollectConfiguration rootConfig) {
    String appId = rootConfig.getAppId();
    List<ServiceCollectConfiguration> multiEnvConfigs = rootConfig.getMultiEnvConfigs();

    List<ServiceCollectConfiguration> configs = this.repositoryProvider.listBy(appId);
    if (CollectionUtils.isEmpty(configs)) {
      configs = createFromGlobalDefault(appId);
    }

    ServiceCollectConfiguration existed = configs.get(0);
    existed.setMultiEnvConfigs(multiEnvConfigs);
    return this.update(existed);
  }

  @Configuration
  @ConfigurationProperties(prefix = "arex.config.default.service.collect")
  static class GlobalServiceCollectConfiguration extends ServiceCollectConfiguration {

  }
}
