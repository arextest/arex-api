package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dto.record.ServiceCollectConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Component
public final class ScheduleConfigurableHandler extends
    AbstractConfigurableHandler<ScheduleConfiguration> {

  private static final String INCLUDE_SERVICE_OPERATIONS = "includeServiceOperations";
  @Resource
  private ScheduleConfiguration globalScheduleConfiguration;

  @Resource
  private ConfigRepositoryProvider<ServiceCollectConfiguration> collectConfigurationProvider;

  protected ScheduleConfigurableHandler(
      @Autowired ConfigRepositoryProvider<ScheduleConfiguration> repositoryProvider) {
    super(repositoryProvider);
  }

  @Override
  public List<ScheduleConfiguration> useResultAsList(String appId) {
    List<ScheduleConfiguration> sourceList = super.useResultAsList(appId);
    if (CollectionUtils.isNotEmpty(sourceList)) {
      ImmutablePair<Set<String>, Set<String>> includeExcludeServiceOperations =
          getIncludeExcludeServiceOperations(appId);
      for (ScheduleConfiguration source : sourceList) {
        source.setIncludeServiceOperationSet(includeExcludeServiceOperations.getLeft());
        source.setExcludeServiceOperationSet(includeExcludeServiceOperations.getRight());
      }
    }
    return sourceList;
  }

  @Override
  public boolean update(ScheduleConfiguration configuration) {
    if (!super.update(configuration)) {
      mergeGlobalDefaultSettings(configuration);
      return super.insert(configuration);
    }
    return true;
  }

  @Override
  public List<ScheduleConfiguration> createFromGlobalDefault(String appId) {
    ScheduleConfiguration scheduleConfiguration = new ScheduleConfiguration();
    scheduleConfiguration.setAppId(appId);
    scheduleConfiguration.setOffsetDays(globalScheduleConfiguration.getOffsetDays());
    scheduleConfiguration.setSendMaxQps(globalScheduleConfiguration.getSendMaxQps());
    scheduleConfiguration.setTargetEnv(globalScheduleConfiguration.getTargetEnv());
    return Collections.singletonList(scheduleConfiguration);
  }

  @Override
  protected boolean shouldMergeGlobalDefault() {
    return false;
  }

  @Override
  protected void mergeGlobalDefaultSettings(ScheduleConfiguration source) {
    if (source.getOffsetDays() == null) {
      source.setOffsetDays(globalScheduleConfiguration.getOffsetDays());
    }
    if (source.getSendMaxQps() == null) {
      source.setSendMaxQps(globalScheduleConfiguration.getSendMaxQps());
    }
    if (source.getTargetEnv() == null) {
      source.setTargetEnv(globalScheduleConfiguration.getTargetEnv());
    }
  }

  private ImmutablePair<Set<String>, Set<String>> getIncludeExcludeServiceOperations(String appId) {
    List<ServiceCollectConfiguration> list = collectConfigurationProvider.listBy(appId);
    if (CollectionUtils.isEmpty(list)) {
      return new ImmutablePair<>(Collections.emptySet(), Collections.emptySet());
    }
    ServiceCollectConfiguration serviceCollect = list.get(0);
    Set<String> includeServiceOperations = new HashSet<>();
    Set<String> excludeServiceOperations = new HashSet<>();
    if (MapUtils.isNotEmpty(serviceCollect.getExtendField())
        && serviceCollect.getExtendField().containsKey(INCLUDE_SERVICE_OPERATIONS)) {
      String includeOperations = serviceCollect.getExtendField().get(INCLUDE_SERVICE_OPERATIONS);
      if (StringUtils.isNotBlank(includeOperations)) {
        includeServiceOperations.addAll(Arrays.asList(StringUtils.split(includeOperations, ",")));
      }
    }
    if (CollectionUtils.isNotEmpty(serviceCollect.getExcludeServiceOperationSet())) {
      excludeServiceOperations = serviceCollect.getExcludeServiceOperationSet();
    }

    return new ImmutablePair<>(includeServiceOperations, excludeServiceOperations);
  }

  @Configuration
  @ConfigurationProperties(prefix = "arex.config.default.schedule")
  static class GlobalScheduleConfiguration extends ScheduleConfiguration {

  }
}