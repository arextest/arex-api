package com.arextest.web.core.business.config;

import com.arextest.config.model.dto.AbstractMultiEnvConfiguration;
import com.arextest.config.repository.MultiEnvConfigRepositoryProvider;

import lombok.Getter;

@Getter
public abstract class AbstractMultiEnvConfigHandler<T extends AbstractMultiEnvConfiguration<?>>
    extends AbstractConfigurableHandler<T>
    implements MultiEnvConfigurableHandler<T> {
  protected MultiEnvConfigRepositoryProvider<T> repositoryProvider;

  protected AbstractMultiEnvConfigHandler(
      MultiEnvConfigRepositoryProvider<T> repositoryProvider) {
    super(repositoryProvider);
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public boolean editMultiEnvList(T rootConfig) {
    return this.getRepositoryProvider().updateMultiEnvConfig(rootConfig);
  }
}
