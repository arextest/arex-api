package com.arextest.report.core.business.configservice.handler;

import java.util.List;

/**
 * @author jmo
 * @since 2022/1/22
 */
public interface ConfigurableHandler<T> extends ViewHandler<T> {
    List<T> editList(String appId);

    boolean insert(T configuration);

    boolean remove(T configuration);

    boolean insertList(List<T> configurationList);

    boolean removeList(List<T> configurationList);

    boolean update(T configuration);
}
