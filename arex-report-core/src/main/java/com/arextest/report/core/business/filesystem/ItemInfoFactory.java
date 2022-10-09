package com.arextest.report.core.business.filesystem;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class ItemInfoFactory {
    private static final String ITEM_INFO = "ItemInfo-";

    @Resource
    private Map<String, ItemInfo> infoMap;

    public ItemInfo getItemInfo(Integer type) {
        String key = ITEM_INFO + type.toString();
        if (infoMap.containsKey(key)) {
            return infoMap.get(key);
        }
        return null;
    }
}
