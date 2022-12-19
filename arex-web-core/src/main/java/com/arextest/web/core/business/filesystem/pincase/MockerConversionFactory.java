package com.arextest.web.core.business.filesystem.pincase;

import org.springframework.stereotype.Component;
import sun.misc.Cache;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author b_yu
 * @since 2022/12/12
 */
@Component
public class MockerConversionFactory {
    private Map<String, MockerConversion> mockerConversions;

    public MockerConversionFactory() {
        ServiceLoader<MockerConversion> cacheConversions = ServiceLoader.load(MockerConversion.class);
        mockerConversions = new ConcurrentHashMap<>();
        for (MockerConversion mockerConversion : cacheConversions) {
            mockerConversions.putIfAbsent(mockerConversion.getCategoryName(), mockerConversion);
        }
    }

    public MockerConversion get(String categoryName) {
        return mockerConversions.get(categoryName);
    }
}
