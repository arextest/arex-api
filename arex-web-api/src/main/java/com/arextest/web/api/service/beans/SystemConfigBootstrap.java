package com.arextest.web.api.service.beans;

import com.arextest.config.model.dao.config.SystemConfigurationCollection.KeySummary;
import com.arextest.config.model.dto.system.SystemConfiguration;
import com.arextest.config.repository.SystemConfigurationRepository;
import com.arextest.web.core.business.ConfigLoadService;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author wildeslam.
 * @create 2024/7/3 16:15
 */
@Data
@Slf4j
@AllArgsConstructor
public class SystemConfigBootstrap implements InitializingBean {

  private ConfigLoadService configLoadService;
  private SystemConfigurationRepository systemConfigurationRepository;


  @Override
  public void afterPropertiesSet() {
    Set<String> ignoreNodeSet = configLoadService.getIgnoreNodeSet("");
    if (ignoreNodeSet == null) {
      ignoreNodeSet = new HashSet<>();
    }
    SystemConfiguration systemConfiguration = new SystemConfiguration();
    systemConfiguration.setKey(KeySummary.IGNORE_NODE_SET);
    systemConfiguration.setIgnoreNodeSet(ignoreNodeSet);
    systemConfigurationRepository.saveConfig(systemConfiguration);
  }


}
