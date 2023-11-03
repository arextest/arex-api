package com.arextest.web.core.repository;

import com.arextest.web.model.dto.MessagePreprocessDto;
import java.util.List;

public interface MessagePreprocessRepository extends RepositoryProvider {

  MessagePreprocessDto update(MessagePreprocessDto dto);

  List<MessagePreprocessDto> queryMessagesByKey(String key);
}
