package com.arextest.web.core.business.config;

import com.arextest.config.model.dto.AbstractMultiEnvConfiguration;

public interface MultiEnvConfigurableHandler<T extends AbstractMultiEnvConfiguration> extends ConfigurableHandler<T> {
  boolean editMultiEnvList(T config);
}
