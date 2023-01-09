package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.replay.ComparisonExclusionsConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonInclusionsConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonListSortConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonReferenceConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonReferenceConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ReplayConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by rchen9 on 2023/1/9.
 */
@Controller
@RequestMapping("/api/config/comparison/summary")
public class ComparisonSummaryController {

    @Resource
    ComparisonExclusionsConfigurableHandler comparisonExclusionsConfigurableHandler;
    @Resource
    ComparisonInclusionsConfigurableHandler comparisonInclusionsConfigurableHandler;
    @Resource
    ComparisonReferenceConfigurableHandler comparisonReferenceConfigurableHandler;
    @Resource
    ComparisonListSortConfigurableHandler comparisonListSortConfigurableHandler;


    @RequestMapping("/queryByInterfaceIdAndOperationId")
    @ResponseBody
    public Response queryByInterfaceIdAndOperationId(@RequestParam String interfaceId,
                                                     @RequestParam(required = false) String operationId) {

        ReplayConfiguration.ReplayComparisonConfig replayComparisonConfig =
                new ReplayConfiguration.ReplayComparisonConfig();
        getComparisonExclusionsConfiguration(interfaceId, operationId, replayComparisonConfig);
        getComparisonInclusionsConfiguration(interfaceId, operationId, replayComparisonConfig);
        getComparisonReferenceConfiguration(interfaceId, operationId, replayComparisonConfig);
        getComparisonListSortConfiguration(interfaceId, operationId, replayComparisonConfig);
        return ResponseUtils.successResponse(replayComparisonConfig);
    }

    private void getComparisonExclusionsConfiguration(String interfaceId, String operationId,
                                                      ReplayConfiguration.ReplayComparisonConfig replayComparisonConfig) {
        Set<List<String>> exclusionSet = new HashSet<>();
        List<ComparisonExclusionsConfiguration> comparisonExclusionsConfigurationList =
                comparisonExclusionsConfigurableHandler.queryByOperationIdAndInterfaceId(interfaceId, operationId);
        Optional.ofNullable(comparisonExclusionsConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            exclusionSet.add(item.getExclusions());
        });
        replayComparisonConfig.setExclusionList(exclusionSet);
    }

    private void getComparisonInclusionsConfiguration(String interfaceId, String operationId,
                                                      ReplayConfiguration.ReplayComparisonConfig replayComparisonConfig) {
        Set<List<String>> inclusionSet = new HashSet<>();
        List<ComparisonInclusionsConfiguration> comparisonInclusionsConfigurationList =
                comparisonInclusionsConfigurableHandler.queryByOperationIdAndInterfaceId(interfaceId, operationId);
        Optional.ofNullable(comparisonInclusionsConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            inclusionSet.add(item.getInclusions());
        });
        replayComparisonConfig.setInclusionList(inclusionSet);
    }

    private void getComparisonReferenceConfiguration(String interfaceId, String operationId,
                                                     ReplayConfiguration.ReplayComparisonConfig replayComparisonConfig) {
        Map<List<String>, List<String>> referenceMap = new HashMap<>();
        List<ComparisonReferenceConfiguration> comparisonReferenceConfigurationList =
                comparisonReferenceConfigurableHandler.queryByOperationIdAndInterfaceId(interfaceId, operationId);
        Optional.ofNullable(comparisonReferenceConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            if (CollectionUtils.isNotEmpty(item.getFkPath())) {
                referenceMap.put(item.getFkPath(), item.getPkPath());
            }
        });
        replayComparisonConfig.setReferenceMap(referenceMap);
    }

    private void getComparisonListSortConfiguration(String interfaceId, String operationId,
                                                    ReplayConfiguration.ReplayComparisonConfig replayComparisonConfig) {
        Map<List<String>, List<List<String>>> listSortMap = new HashMap<>();
        List<ComparisonListSortConfiguration> comparisonListSortConfigurationList =
                comparisonListSortConfigurableHandler.queryByOperationIdAndInterfaceId(interfaceId, operationId);
        Optional.ofNullable(comparisonListSortConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            if (CollectionUtils.isNotEmpty(item.getListPath())) {
                listSortMap.put(item.getListPath(), item.getKeys());
            }
        });
        replayComparisonConfig.setListSortMap(listSortMap);
    }

}
