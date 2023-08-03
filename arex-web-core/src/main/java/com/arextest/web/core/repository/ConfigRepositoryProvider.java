package com.arextest.web.core.repository;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author jmo
 * @since 2022/1/25
 */
public interface  ConfigRepositoryProvider<T> {
    List<T> list();

    List<T> listBy(String appId);

    boolean update(T configuration);

    boolean remove(T configuration);

    boolean insert(T configuration);

    default List<T> listBy(String appId, String operationId) {
        return null;
    }

    default List<T> queryByInterfaceIdAndOperationId(String interfaceId,String operationId){
        return null;
    }

    default boolean insertList(List<T> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)) {
            return false;
        }
        for (T configuration : configurationList) {
            this.insert(configuration);
        }
        return true;
    }

    default boolean removeList(List<T> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)) {
            return false;
        }
        for (T configuration : configurationList) {
            this.remove(configuration);
        }
        return true;
    }

    default boolean removeByAppId(String appId) {
        return false;
    }

    default long count(String appId) {
        return 0;
    }
}

