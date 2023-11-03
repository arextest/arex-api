package com.arextest.web.core.repository;

import com.arextest.web.model.dto.ServletMockerDto;
import java.util.List;

public interface ServletMockerRepository extends RepositoryProvider {

  List<ServletMockerDto> queryServletMockers(String index, Integer step);
}
