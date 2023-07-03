package com.arextest.web.core.repository;

import com.arextest.web.model.dto.ApplicationDto;

import java.util.List;

public interface ApplicationRepository extends RepositoryProvider {

    boolean saveApplicationList(List<ApplicationDto> applicationDtos);

    List<ApplicationDto> queryApplicationList(String operationId);
}
