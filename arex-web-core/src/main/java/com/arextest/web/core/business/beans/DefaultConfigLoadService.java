package com.arextest.web.core.business.beans;

import com.arextest.web.core.business.ConfigLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class DefaultConfigLoadService implements ConfigLoadService {

  private Environment environment;

  public DefaultConfigLoadService(@Autowired Environment environment) {
    this.environment = environment;

  }

  @Override
  public Object getProperty(String key, Object defaultValue) {
    if (environment.getProperty(key) == null) {
      return defaultValue;
    }
    return environment.getProperty(key);
  }
}
