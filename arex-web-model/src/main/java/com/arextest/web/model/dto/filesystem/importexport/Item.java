package com.arextest.web.model.dto.filesystem.importexport;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface Item {
    List<Item> getItems();
    void setItems(List<Item> items);
    String getNodeName();
    void setNodeName(String nodeName);
    Integer getNodeType();
    void setNodeType(Integer nodeType);
}
