package com.arextest.web.core.business.filesystem;

import java.util.Map;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

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
