package com.arextest.web.core.repository;

import java.util.List;

import com.arextest.web.model.dto.MessagePreprocessDto;

public interface MessagePreprocessRepository extends RepositoryProvider {
    MessagePreprocessDto update(MessagePreprocessDto dto);

    List<MessagePreprocessDto> queryMessagesByKey(String key);
}
