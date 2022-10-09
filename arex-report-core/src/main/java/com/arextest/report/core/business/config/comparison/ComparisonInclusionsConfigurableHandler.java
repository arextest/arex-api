package com.arextest.report.core.business.config.comparison;

import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.model.api.contracts.config.replay.ComparisonInclusionsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by rchen9 on 2022/9/16.
 */
@Component
public class ComparisonInclusionsConfigurableHandler extends AbstractComparisonConfigurableHandler<ComparisonInclusionsConfiguration> {
    protected ComparisonInclusionsConfigurableHandler(@Autowired ConfigRepositoryProvider<ComparisonInclusionsConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }
}
