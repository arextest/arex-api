package com.arextest.web.core.repository;

import java.util.List;

import com.arextest.web.model.dto.ServletMockerDto;

public interface ServletMockerRepository extends RepositoryProvider {
    List<ServletMockerDto> queryServletMockers(String index, Integer step);
}
