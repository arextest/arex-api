package com.arextest.report.core.repository;


import com.arextest.report.model.dto.MessagePreprocessDto;

import java.util.List;

public interface MessagePreprocessRepository extends RepositoryProvider {
    MessagePreprocessDto update(MessagePreprocessDto dto);
    List<MessagePreprocessDto> queryMessagesByKey(String key);
}
