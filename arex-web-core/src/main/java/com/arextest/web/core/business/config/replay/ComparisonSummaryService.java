package com.arextest.web.core.business.config.replay;

import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonReferenceConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ReplayConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by rchen9 on 2023/2/7.
 */
@Component
public class ComparisonSummaryService {

    @Resource
    ComparisonExclusionsConfigurableHandler comparisonExclusionsConfigurableHandler;
    @Resource
    ComparisonInclusionsConfigurableHandler comparisonInclusionsConfigurableHandler;
    @Resource
    ComparisonReferenceConfigurableHandler comparisonReferenceConfigurableHandler;
    @Resource
    ComparisonListSortConfigurableHandler comparisonListSortConfigurableHandler;


    public ReplayConfiguration.ReplayComparisonConfig getComparisonDetailsSummary(String interfaceId) {
        ReplayConfiguration.ReplayComparisonConfig replayComparisonConfig =
                new ReplayConfiguration.ReplayComparisonConfig();
        getComparisonExclusionsConfiguration(interfaceId, replayComparisonConfig);
        getComparisonInclusionsConfiguration(interfaceId, replayComparisonConfig);
        getComparisonListSortConfiguration(interfaceId, replayComparisonConfig);
        getComparisonReferenceConfiguration(interfaceId, replayComparisonConfig);
        return replayComparisonConfig;
    }


    private void getComparisonExclusionsConfiguration(String interfaceId,
                                                      ReplayConfiguration.ReplayComparisonConfig replayComparisonConfig) {
        Set<List<String>> exclusionSet = new HashSet<>();
        List<ComparisonExclusionsConfiguration> comparisonExclusionsConfigurationList =
                comparisonExclusionsConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonExclusionsConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            exclusionSet.add(item.getExclusions());
        });
        replayComparisonConfig.setExclusionList(exclusionSet);
    }

    private void getComparisonInclusionsConfiguration(String interfaceId,
                                                      ReplayConfiguration.ReplayComparisonConfig replayComparisonConfig) {
        Set<List<String>> inclusionSet = new HashSet<>();
        List<ComparisonInclusionsConfiguration> comparisonInclusionsConfigurationList =
                comparisonInclusionsConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonInclusionsConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            inclusionSet.add(item.getInclusions());
        });
        replayComparisonConfig.setInclusionList(inclusionSet);
    }

    private void getComparisonListSortConfiguration(String interfaceId,
                                                    ReplayConfiguration.ReplayComparisonConfig replayComparisonConfig) {
        Map<List<String>, List<List<String>>> listSortMap = new HashMap<>();
        List<ComparisonListSortConfiguration> comparisonListSortConfigurationList =
                comparisonListSortConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonListSortConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            if (CollectionUtils.isNotEmpty(item.getListPath())) {
                listSortMap.put(item.getListPath(), item.getKeys());
            }
        });
        replayComparisonConfig.setListSortMap(listSortMap);
    }

    private void getComparisonReferenceConfiguration(String interfaceId,
                                                     ReplayConfiguration.ReplayComparisonConfig replayComparisonConfig) {
        Map<List<String>, List<String>> referenceMap = new HashMap<>();
        List<ComparisonReferenceConfiguration> comparisonReferenceConfigurationList =
                comparisonReferenceConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonReferenceConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            if (CollectionUtils.isNotEmpty(item.getFkPath())) {
                referenceMap.put(item.getFkPath(), item.getPkPath());
            }
        });
        replayComparisonConfig.setReferenceMap(referenceMap);
    }


}
