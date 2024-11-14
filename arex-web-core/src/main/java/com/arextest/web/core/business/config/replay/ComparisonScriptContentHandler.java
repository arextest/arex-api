package com.arextest.web.core.business.config.replay;

import com.arextest.web.core.repository.mongo.ComparisonScriptContentRepository;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonScriptContentRequestType;
import com.arextest.web.model.dto.config.ComparisonScriptContent;
import com.arextest.web.model.mapper.ConfigComparisonScriptContentMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ComparisonScriptContentHandler {


  private final ComparisonScriptContentRepository comparisonScriptContentRepository;

  public List<ComparisonScriptContent> queryScriptMethodNames() {
    return comparisonScriptContentRepository.queryScriptMethodNames();
  }

  public boolean saveScriptContent(ComparisonScriptContentRequestType request) {
    ComparisonScriptContent dto =
        ConfigComparisonScriptContentMapper.INSTANCE.dtoFromContract(request);
    return comparisonScriptContentRepository.save(dto);
  }

  public List<ComparisonScriptContent> queryAll() {
    return comparisonScriptContentRepository.queryAll();
  }
}
