package com.arextest.report.core.business.filesystem;


public interface ItemInfo {
    String saveItem(String parentId, Integer parentNodeType);
    Boolean removeItem(String infoId);
    String duplicate(String parentId, String infoId);
}
