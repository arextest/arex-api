package com.arextest.web.core.repository;

import com.arextest.web.model.contract.contracts.config.SystemConfig;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2023/9/25 17:27
 */
public interface SystemConfigRepository extends RepositoryProvider {
    List<SystemConfig> listSystemConfigs();

    SystemConfig queryByType(Integer systemConfigType);

    boolean saveList(List<SystemConfig> systemConfigDtos);
}
