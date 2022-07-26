package com.arextest.report.core.business.filesystem;


import java.util.Set;

public interface ItemInfo {
    String saveItem(String parentId, Integer parentNodeType);
    Boolean removeItem(String infoId);
    Boolean removeItems(Set<String> infoIds);
    String duplicate(String parentId, String infoId);
}
