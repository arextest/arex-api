package com.arextest.report.core.repository;


import com.arextest.report.model.dto.ServletMockerDto;

import java.util.List;

public interface ServletMockerRepository extends RepositoryProvider {
    List<ServletMockerDto> queryServletMockers(String index, Integer step);
}
