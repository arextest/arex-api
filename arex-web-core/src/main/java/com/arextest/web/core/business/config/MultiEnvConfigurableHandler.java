package com.arextest.web.core.business.config;

import com.arextest.config.model.dto.record.MultiEnvConfig;
import java.util.List;

public interface MultiEnvConfigurableHandler<T extends MultiEnvConfig<T>> extends ConfigurableHandler<T> {
  boolean editMultiEnvList(T config);
}
