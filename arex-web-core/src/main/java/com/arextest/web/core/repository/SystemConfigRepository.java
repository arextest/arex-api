package com.arextest.web.core.repository;

import com.arextest.web.model.contract.contracts.config.SystemConfiguration;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2023/9/25 17:27
 */
public interface SystemConfigRepository extends RepositoryProvider {

  boolean saveConfig(SystemConfiguration systemConfig);

  List<SystemConfiguration> getAllSystemConfigList();

  SystemConfiguration getSystemConfigByKey(String key);
}
