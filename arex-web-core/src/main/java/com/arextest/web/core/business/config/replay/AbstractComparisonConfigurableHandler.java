package com.arextest.web.core.business.config.replay;

import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.model.contract.contracts.common.enums.CompareConfigType;
import com.arextest.web.model.contract.contracts.common.enums.ExpirationType;
import com.arextest.web.model.contract.contracts.config.replay.AbstractComparisonDetailsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jmo
 * @since 2022/1/22
 */
public abstract class AbstractComparisonConfigurableHandler<T extends AbstractComparisonDetailsConfiguration>
        extends AbstractConfigurableHandler<T> {

    protected AbstractComparisonConfigurableHandler(ConfigRepositoryProvider<T> repositoryProvider) {
        super(repositoryProvider);
    }

    @Override
    public List<T> useResultAsList(String appId) {
        List<T> comparisonDetails = repositoryProvider.listBy(appId);
        if (CollectionUtils.isNotEmpty(comparisonDetails)) {
            comparisonDetails.removeIf(this::removeDetailsExpired);
        }
        return comparisonDetails;
    }

    public List<T> useResultAsList(String appId, String operationId) {
        List<T> comparisonDetails = repositoryProvider.listBy(appId, operationId);
        if (CollectionUtils.isNotEmpty(comparisonDetails)) {
            comparisonDetails.removeIf(this::removeDetailsExpired);
        }
        return comparisonDetails;
    }

    public List<T> useResultAsList(String appId, int compareConfigType) {
        return this.useResultAsList(appId).stream()
                .filter(config -> config.getCompareConfigType() == compareConfigType)
                .collect(Collectors.toList());
    }

    public List<T> queryByOperationIdAndInterfaceId(String interfaceId, String operationId) {
        List<T> comparisonDetails = repositoryProvider.queryByInterfaceIdAndOperationId(interfaceId, operationId);
        if (CollectionUtils.isNotEmpty(comparisonDetails)) {
            comparisonDetails.removeIf(this::removeDetailsExpired);
        }
        return comparisonDetails;
    }

    public abstract List<T> queryByInterfaceId(String interfaceId);


    public List<T> queryComparisonConfig(String appId, String operationId, String dependencyId) {

        // query the config of dependency
        if (dependencyId != null) {
            List<T> comparisonConfigList = this.useResultAsList(appId, operationId);
            return comparisonConfigList.stream()
                    .filter(config -> Objects.equals(config.getDependencyId(), dependencyId))
                    .collect(Collectors.toList());
        }

        // query the config of operation
        if (operationId != null) {
            List<T> comparisonConfigList =  this.useResultAsList(appId, operationId);
            return comparisonConfigList.stream()
                    .filter(config -> Objects.equals(
                            config.getCompareConfigType(), CompareConfigType.REPLAY_MAIN.getCodeValue()))
                    .collect(Collectors.toList());
        }

        // query the config of app global
        return this.useResultAsList(appId, null);
    }

    private boolean removeDetailsExpired(T comparisonDetails) {
        int expirationType = comparisonDetails.getExpirationType();
        if (expirationType == ExpirationType.ABSOLUTE_TIME_EXPIRED.getCodeValue()) {
            return comparisonDetails.getExpirationDate().getTime() < System.currentTimeMillis();
        }
        return expirationType != ExpirationType.PINNED_NEVER_EXPIRED.getCodeValue();
    }

    @Override
    public boolean insert(T comparisonDetail) {

        if (comparisonDetail.getExpirationDate() == null) {
            comparisonDetail.setExpirationDate(new Date());
        }
        return repositoryProvider.insert(comparisonDetail);
    }

    @Override
    public boolean insertList(List<T> configurationList) {
        List<T> configurations = Optional.ofNullable(configurationList)
                .map(List::stream).orElse(Stream.empty())
                .filter(item -> item != null && StringUtils.isNotEmpty(item.getAppId()))
                .peek(item -> {
                    if (item.getExpirationDate() == null) {
                        item.setExpirationDate(new Date());
                    }
                }).collect(Collectors.toList());

        return repositoryProvider.insertList(configurations);
    }

    public boolean removeByAppId(String appId) {
        return repositoryProvider.listBy(appId).isEmpty() || repositoryProvider.removeByAppId(appId);
    }
}
