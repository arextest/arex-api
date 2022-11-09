package com.arextest.report.core.business.config.replay;

import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.model.api.contracts.config.replay.ComparisonReferenceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Component
public class ComparisonReferenceConfigurableHandler extends AbstractComparisonConfigurableHandler<ComparisonReferenceConfiguration> {
    protected ComparisonReferenceConfigurableHandler(@Autowired ConfigRepositoryProvider<ComparisonReferenceConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }
}
