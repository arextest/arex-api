package com.arextest.web.core.business.expectation;

import com.arextest.web.core.repository.expectation.ExpectationScriptRepository;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptDeleteRequest;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptModel;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptQueryRequest;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @since 2023/12/15
 */
@Service
public class ExpectationScriptService {
    private final ExpectationScriptRepository repository;
    private final ScriptNormalizer normalizer;

    public ExpectationScriptService(ExpectationScriptRepository repository, ScriptNormalizer normalizer) {
        this.repository = repository;
        this.normalizer = normalizer;
    }

    public List<ExpectationScriptModel> query(ExpectationScriptQueryRequest request) {
        return repository.query(request);
    }

    public boolean save(ExpectationScriptModel model) {
        boolean result = normalizer.normalize(model);
        if (!result) {
            return false;
        }
        return model.getId() != null ? repository.update(model) : repository.insert(model);
    }

    public boolean delete(ExpectationScriptDeleteRequest request) {
        return repository.delete(request.getId(), request.getAppId());
    }
}
