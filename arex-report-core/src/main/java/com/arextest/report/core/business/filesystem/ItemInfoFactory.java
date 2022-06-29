package com.arextest.report.core.business.filesystem;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class ItemInfoFactory {
    @Resource
    private Map<String, ItemInfo> infoMap;

    public ItemInfo getItemInfo(Integer type) {
        if (infoMap.containsKey(type.toString())) {
            return infoMap.get(type.toString());
        }
        return null;
    }
}
