package com.arextest.web.core.repository;

import com.arextest.web.model.dto.DesensitizationJarDto;
import java.util.List;

/**
 * @author qzmo
 * @since 2023/8/16
 */
public interface DesensitizationJarRepository extends RepositoryProvider {

  boolean saveJar(DesensitizationJarDto dto);

  boolean deleteJar(String jarId);

  boolean deleteAll();

  List<DesensitizationJarDto> queryAll();
}
