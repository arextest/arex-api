package com.arextest.web.core.repository;

import com.arextest.web.model.dto.PreprocessConfigDto;

/**
 * @author b_yu
 * @since 2022/6/6
 */
public interface PreprocessConfigRepository extends RepositoryProvider {
    PreprocessConfigDto updateIndex(String name, String index);

    PreprocessConfigDto queryConfig(String name);
}
